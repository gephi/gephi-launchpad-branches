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

import java.util.ArrayList;
import java.util.List;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.project.api.WorkspaceListener;
import org.gephi.project.api.WorkspaceProvider;
import org.gephi.similarity.api.SimilarityController;
import org.gephi.similarity.api.SimilarityModel;
import org.gephi.similarity.spi.Similarity;
import org.gephi.similarity.spi.SimilarityBuilder;
import org.gephi.similarity.spi.SimilarityUI;
import org.gephi.utils.longtask.api.LongTaskExecutor;
import org.gephi.utils.longtask.api.LongTaskListener;
import org.gephi.utils.longtask.spi.LongTask;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * 
 *
 * @author Cezary Bartosiak
 */
@ServiceProvider(service = SimilarityController.class)
public class SimilarityControllerImpl implements SimilarityController {
	private SimilarityBuilder[] similarityBuilders;
	private SimilarityModelImpl model;

	public SimilarityControllerImpl() {
		similarityBuilders = Lookup.getDefault().lookupAll(SimilarityBuilder.class).toArray(new SimilarityBuilder[0]);

		// Workspace events
		ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
		pc.addWorkspaceListener(new WorkspaceListener() {
			@Override
			public void initialize(Workspace workspace) {
				workspace.add(new SimilarityModelImpl());
			}

			@Override
			public void select(Workspace workspace) {
				model = workspace.getLookup().lookup(SimilarityModelImpl.class);
				if (model == null) {
					model = new SimilarityModelImpl();
					workspace.add(model);
				}
			}

			@Override
			public void unselect(Workspace workspace) {
				
			}

			@Override
			public void close(Workspace workspace) {
				
			}

			@Override
			public void disable() {
				model = null;
			}
		});

		if (pc.getCurrentWorkspace() != null) {
			model = pc.getCurrentWorkspace().getLookup().lookup(SimilarityModelImpl.class);
			if (model == null) {
				model = new SimilarityModelImpl();
				pc.getCurrentWorkspace().add(model);
			}
		}
	}

	@Override
	public void execute(final Similarity similarity, LongTaskListener listener) {
		ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
		Workspace[] workspaces = pc.getCurrentProject().getLookup().lookup(WorkspaceProvider.class).getWorkspaces();

		final GraphController     graphController       = Lookup.getDefault().lookup(GraphController.class);
		final GraphModel          sourceGraphModel      = graphController.getModel();
		final GraphModel[]        targetGraphModels     = new GraphModel[workspaces.length];
		final AttributeController attributeController   = Lookup.getDefault().lookup(AttributeController.class);
		final AttributeModel      sourceAttributeModel  = attributeController.getModel();
		final AttributeModel[]    targetAttributeModels = new AttributeModel[workspaces.length];
		final SimilarityUI[]      uis                   = getUI(similarity);
		final String[]            graphNames            = new String[workspaces.length];

		for (int i = 0; i < workspaces.length; ++i) {
			if (workspaces[i] != pc.getCurrentWorkspace())
				graphNames[i] = "Target graph " + i;
			else graphNames[i] = "Source graph";
			
			targetGraphModels[i]     = graphController.getModel(workspaces[i]);
			targetAttributeModels[i] = attributeController.getModel(workspaces[i]);
		}

		SimilarityBuilder builder = getBuilder(similarity.getClass());

		for (SimilarityUI s : uis)
			s.setup(similarity);
		model.setRunning(similarity, true);

		if (similarity instanceof LongTask) {
			LongTaskExecutor executor = new LongTaskExecutor(true, builder.getName(), 10);
			if (listener != null)
				executor.setLongTaskListener(listener);
			executor.execute((LongTask)similarity, new Runnable() {
				@Override
				public void run() {
					similarity.execute(sourceGraphModel, targetGraphModels,
							sourceAttributeModel, targetAttributeModels, graphNames);
					model.setRunning(similarity, false);
					for (SimilarityUI s : uis) {
						model.addResult(s);
						s.unsetup();
					}
					model.addReport(similarity);
				}
			}, builder.getName(), null);
		}
		else {
			similarity.execute(sourceGraphModel, targetGraphModels,
					sourceAttributeModel, targetAttributeModels, graphNames);
			if (listener != null)
				listener.taskFinished(null);
			model.setRunning(similarity, false);
			for (SimilarityUI s : uis) {
				model.addResult(s);
				s.unsetup();
			}
			model.addReport(similarity);
		}
	}

	public SimilarityUI[] getUI(Similarity similarity) {
		List<SimilarityUI> list = new ArrayList<SimilarityUI>();
		for (SimilarityUI sui : Lookup.getDefault().lookupAll(SimilarityUI.class))
			if (sui.getSimilarityClass().equals(similarity.getClass()))
				list.add(sui);
		return list.toArray(new SimilarityUI[0]);
	}

	@Override
	public SimilarityBuilder getBuilder(Class<? extends Similarity> similarity) {
		for (SimilarityBuilder b : similarityBuilders)
			if (b.getSimilarityClass().equals(similarity))
				return b;
		return null;
	}

	@Override
	public void setSimilarityUIVisible(SimilarityUI ui, boolean visible) {
		model.setVisible(ui, visible);
	}

	@Override
	public SimilarityModel getModel() {
		return model;
	}
}
