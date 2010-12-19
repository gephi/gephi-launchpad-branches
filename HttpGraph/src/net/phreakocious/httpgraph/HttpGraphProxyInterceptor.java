package net.phreakocious.httpgraph;

import com.predic8.membrane.core.exchange.Exchange;
import com.predic8.membrane.core.interceptor.AbstractInterceptor;
import com.predic8.membrane.core.interceptor.Outcome;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tb
 */
public class HttpGraphProxyInterceptor extends AbstractInterceptor {

    private static final Logger log = Logger.getLogger(HttpGraphProxyInterceptor.class.getName());

    public HttpGraphProxyInterceptor() {
        log.log(Level.INFO, "Snarfing!");
    }

    @Override
    public Outcome handleResponse(Exchange exchange) throws Exception {
        String srcaddr = exchange.getSourceHostname();
        String uri = exchange.getOriginalRequestUri();
        String method = exchange.getRequest().getMethod();
        String host = exchange.getServer();
        String type = exchange.getResponseContentType();
        String referer = exchange.getRequest().getHeader().getFirstValue("Referer");
        int statuscode = exchange.getResponse().getStatusCode();

        int bytes = exchange.getRequestContentLength() + exchange.getResponseContentLength();

        SnarfData sd = new SnarfData(srcaddr, uri, method, host, referer);
        sd.getNode("uri").setAttrib("bytes", bytes);
        sd.getNode("uri").setAttrib("content-type", type);
        sd.getNode("uri").setAttrib("status code", statuscode);

        sd.nullCheck();
        sd.graphUpdate();
        return Outcome.CONTINUE;
    }
}
