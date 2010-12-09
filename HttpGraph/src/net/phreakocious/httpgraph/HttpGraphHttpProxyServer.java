package net.phreakocious.httpgraph;

import java.lang.Thread.UncaughtExceptionHandler;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
//import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ContainerLoader;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.littleshoot.proxy.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpGraphHttpProxyServer implements HttpProxyServer {

    private Logger log =
        LoggerFactory.getLogger(HttpGraphHttpProxyServer.class);

    private final ChannelGroup allChannels =
        new DefaultChannelGroup("HTTPGraph");

    private final int port;

    private final ProxyAuthorizationManager authenticationManager =
        new HttpGraphProxyAuthorizationManager();

    private final Map<String, HttpFilter> filters;

    private final String chainProxyHostAndPort;

    //private Container gephicontainer;
    private ContainerLoader cldr;

    public void setContainer(ContainerLoader c) {
        //gephicontainer = c;
        cldr = c;
    }

    

    /**
     * Creates a new proxy server.
     *
     * @param port The port the server should run on.
     */
    public HttpGraphHttpProxyServer(final int port) {
        this(port, new HashMap<String, HttpFilter>());
    }

    public HttpGraphHttpProxyServer(final int port,
        final Map<String, HttpFilter> filters) {
        this(port, filters, null);
    }

    /**
     * Creates a new proxy server.
     *
     * @param port The port the server should run on.
     * @param filters HTTP filters to apply.
     */
    public HttpGraphHttpProxyServer(final int port,
        final Map<String, HttpFilter> filters,
        final String chainProxyHostAndPort) {
        this.port = port;
        this.filters = Collections.unmodifiableMap(filters);
        this.chainProxyHostAndPort = chainProxyHostAndPort;
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(final Thread t, final Throwable e) {
                log.error("Uncaught exception", e);
            }
        });
    }

    @Override
    public void start() {
        log.info("Starting proxy on port: "+this.port);
        //cldr = gephicontainer.getLoader();
        ((HttpGraphProxyAuthorizationManager)authenticationManager).setContainer(cldr);
        final ServerBootstrap bootstrap = new ServerBootstrap(
            new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));

        final HttpServerPipelineFactory factory =
            new HttpServerPipelineFactory(authenticationManager,
                this.allChannels, this.filters, this.chainProxyHostAndPort);
        bootstrap.setPipelineFactory(factory);
        final Channel channel = bootstrap.bind(new InetSocketAddress(port));
        allChannels.add(channel);

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                final ChannelGroupFuture future = allChannels.close();
                future.awaitUninterruptibly(120*1000);
                bootstrap.releaseExternalResources();
            }
        }));
    }

    @Override
    public void addProxyAuthenticationHandler(
        final ProxyAuthorizationHandler pah) {
        this.authenticationManager.addHandler(pah);
    }
}