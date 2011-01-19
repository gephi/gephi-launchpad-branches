/*
 * Copyright 2008-2010 Gephi
 * Authors : Cezary Bartosiak
 * Website : http://www.gephi.org
 * 
 * This file is part of Gephi.
 *
 * Gephi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Gephi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.similarity;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.codec.binary.Base64;
import org.gephi.similarity.api.SimilarityModel;
import org.gephi.similarity.spi.Similarity;
import org.gephi.similarity.spi.SimilarityBuilder;
import org.gephi.similarity.spi.SimilarityUI;
import org.gephi.utils.TempDirUtils;
import org.gephi.utils.TempDirUtils.TempDir;
import org.openide.util.Lookup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 *
 * @author Cezary Bartosiak
 */
public class SimilarityModelImpl implements SimilarityModel {
	private final List<SimilarityUI>        invisibleList;
	private final List<Similarity>          runningList;
	private final Map<SimilarityUI, String> resultMap;
	private final Map<Class, String>        reportMap;

	private final List<ChangeListener> listeners;

	public SimilarityModelImpl() {
		invisibleList = new ArrayList<SimilarityUI>();
		runningList   = Collections.synchronizedList(new ArrayList<Similarity>());
		resultMap     = new HashMap<SimilarityUI, String>();
		reportMap     = new HashMap<Class, String>();

		listeners = new ArrayList<ChangeListener>();
	}

	public void addReport(Similarity similarity) {
		reportMap.put(similarity.getClass(), similarity.getReport());
		fireChangeEvent();
	}

	public void addResult(SimilarityUI ui) {
		if (resultMap.containsKey(ui) && ui.getValue() == null)
			resultMap.remove(ui);
		else resultMap.put(ui, ui.getValue());
		fireChangeEvent();
	}

	public void setRunning(Similarity similarity, boolean running) {
		if (!running) {
			if (runningList.remove(similarity))
				fireChangeEvent();
		}
		else if (!runningList.contains(similarity)) {
			runningList.add(similarity);
			fireChangeEvent();
		}
	}

	public void setVisible(SimilarityUI similarityUI, boolean visible) {
		if (visible) {
			if (invisibleList.remove(similarityUI))
				fireChangeEvent();
		}
		else if (!invisibleList.contains(similarityUI)) {
			invisibleList.add(similarityUI);
			fireChangeEvent();
		}
	}

	@Override
	public String getReport(Class<? extends Similarity> similarity) {
		return reportMap.get(similarity);
	}

	@Override
	public String getResult(SimilarityUI similarityUI) {
		return resultMap.get(similarityUI);
	}

	@Override
	public boolean isSimilarityUIVisible(SimilarityUI similarityUI) {
		return !invisibleList.contains(similarityUI);
	}

	@Override
	public boolean isRunning(SimilarityUI similarityUI) {
		for (Similarity s : runningList)
			if (similarityUI.getSimilarityClass().equals(s.getClass()))
				return true;
		return false;
	}

	@Override
	public Similarity getRunning(SimilarityUI similarityUI) {
		for (Similarity s : runningList)
			if (similarityUI.getSimilarityClass().equals(s.getClass()))
				return s;
		return null;
	}

	@Override
	public void addChangeListener(ChangeListener changeListener) {
		if (!listeners.contains(changeListener))
			listeners.add(changeListener);
	}

	@Override
	public void removeChangeListener(ChangeListener changeListener) {
		listeners.remove(changeListener);
	}

	public void fireChangeEvent() {
		ChangeEvent evt = new ChangeEvent(this);
		for (ChangeListener listener : listeners)
			listener.stateChanged(evt);
	}

	public Element writeXML(Document document) {
		Element modelE = document.createElement("similaritymodel");

		Element resultsE = document.createElement("results");
		for (Map.Entry<SimilarityUI, String> entry : resultMap.entrySet())
			if (entry.getValue() != null && !entry.getValue().isEmpty()) {
				Element resultE = document.createElement("result");
				resultE.setAttribute("class", entry.getKey().getClass().getName());
				resultE.setAttribute("value", entry.getValue());
				resultsE.appendChild(resultE);
			}
		modelE.appendChild(resultsE);

		Element reportsE = document.createElement("reports");
		for (Map.Entry<Class, String> entry : reportMap.entrySet())
			if (entry.getValue() != null && !entry.getValue().isEmpty()) {
				Element reportE = document.createElement("report");
				String  report  = entry.getValue();
				report = embedImages(report);
				reportE.setAttribute("class", entry.getKey().getName());
				reportE.setAttribute("value", report);
				reportsE.appendChild(reportE);
			}
		modelE.appendChild(reportsE);

		return modelE;
	}

	public void readXML(Element modelE) {
		Element resultsE = getNextElementByTagName(modelE, "results");
		if (resultsE != null) {
			Collection<? extends SimilarityUI> uis = Lookup.getDefault().lookupAll(SimilarityUI.class);
			for (Element resultE : getElementsByTagName(resultsE, "result")) {
				String       classStr = resultE.getAttribute("class");
				SimilarityUI resultUI = null;
				for (SimilarityUI ui : uis)
					if (ui.getClass().getName().equals(classStr))
						resultUI = ui;
				if (resultUI != null) {
					String value = resultE.getAttribute("value");
					resultMap.put(resultUI, value);
				}
			}
		}

		Element reportsE = getNextElementByTagName(modelE, "reports");
		if (reportsE != null) {
			Collection<? extends SimilarityBuilder> builders = Lookup.getDefault().lookupAll(SimilarityBuilder.class);
			for (Element reportE : getElementsByTagName(reportsE, "report")) {
				String classStr    = reportE.getAttribute("class");
				Class  reportClass = null;
				for (SimilarityBuilder builder : builders)
					if (builder.getSimilarityClass().getName().equals(classStr))
						reportClass = builder.getSimilarityClass();
				if (reportClass != null) {
					String report = reportE.getAttribute("value");
					report = unembedImages(report);
					reportMap.put(reportClass, report);
				}
			}
		}
	}

	private Element getNextElementByTagName(Element node, String name) {
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); ++i) {
			Node n = list.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equalsIgnoreCase(name))
				return (Element) n;
		}
		return null;
	}

	private Element[] getElementsByTagName(Element node, String name) {
		NodeList list = node.getElementsByTagName(name);
		Element[] res = new Element[list.getLength()];
		for (int i = 0; i < list.getLength(); ++i) {
			Node n = list.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE)
				res[i] = (Element)n;
		}
		return res;
	}

	private String embedImages(String report) {
		StringBuilder builder = new StringBuilder();
		String[] result = report.split("file:");
		boolean first = true;
		for (int i = 0; i < result.length; ++i)
			if (result[i].contains("</IMG>")) {
				String next = result[i];
				String[] elements = next.split("\"");
				String filename = elements[0];

				ByteArrayOutputStream out = new ByteArrayOutputStream();

				File file = new File(filename);
				try {
					BufferedImage image = ImageIO.read(file);
					ImageIO.write((RenderedImage)image, "PNG", out);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				byte[] imageBytes = out.toByteArray();
				String base64String = Base64.encodeBase64String(imageBytes);
				if (!first)
					builder.append("\"");
				first = false;
				builder.append("data:image/png;base64,");
				builder.append(base64String);
				for (int j = 1; j < elements.length; ++j) {
					builder.append("\"");
					builder.append(elements[j]);
				}
			}
			else builder.append(result[i]);
		return builder.toString();
	}

	private String unembedImages(String report) {
		StringBuilder builder = new StringBuilder();
		String[] result = report.split("data:image/png;base64");
		if (result.length == 0)
			return report;
		try {
			TempDir tempDir = TempDirUtils.createTempDir();

			for (int i = 0; i < result.length; ++i)
				if (result[i].contains("</IMG>")) {
					String next = result[i];
					int endIndex = next.indexOf('\"');
					String pngStr = next.substring(0, endIndex);
					byte[] imageBytes = Base64.decodeBase64(pngStr);
					String fileName = "image" + i + ".png";
					File file = tempDir.createFile(fileName);

					FileOutputStream fos = new FileOutputStream(file);
					fos.write(imageBytes);

					String path = "file:" + file.getAbsolutePath();
					builder.append(path);

					builder.append(next.substring(endIndex, next.length()));
				}
				else builder.append(result[i]);
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		return builder.toString();
	}
}
