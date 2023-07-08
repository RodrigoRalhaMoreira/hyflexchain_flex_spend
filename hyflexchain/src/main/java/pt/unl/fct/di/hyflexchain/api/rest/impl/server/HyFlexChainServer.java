package pt.unl.fct.di.hyflexchain.api.rest.impl.server;

import java.io.File;
import java.net.URI;
import java.util.Arrays;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import pt.unl.fct.di.hyflexchain.api.rest.impl.server.resources.HyFlexChainResource;
import pt.unl.fct.di.hyflexchain.planes.application.ApplicationInterface;

public class HyFlexChainServer
{
	public static final int MIN_ARGS = 3;

	/**
	 * args[0] -> replicaId
	 * args[1] -> Server Port
	 * args[2] -> config folder
	 * @param args
	 */
	public static void main(String[] args) {
		try
		{
			if (args.length < MIN_ARGS)
			{
				System.err.println("Invalid parameters:\nUsage: <replicaId> <bind port> <config_folder> [<config>]");
				System.exit(1);
			}

			// int replicaId = Integer.parseInt(args[0]);
			int port = Integer.parseInt(args[1]);

			ApplicationInterface app = new ApplicationInterface(new File(args[2]),
				Arrays.copyOfRange(args, MIN_ARGS, args.length, String[].class));
			
			HyFlexChainResource.setHyflexchainInterface(app);
            
			// URI uri = new URI(String.format("https://%s:%d/api/rest", ip, port));
			URI uri = new URI(String.format("http://%s:%d/api/rest", "0.0.0.0", port));

			ResourceConfig config = new ResourceConfig();
			config.register(HyFlexChainResource.class);
            
			// SSLContext sslContext = ServerConfig.getSSLContext();
			JdkHttpServerFactory.createHttpServer(uri, config);

			System.out.println("\n\n################################################");
			System.out.println("HyFlexChain Server is running on " + uri.toString());
			System.out.println("HyFlexChain address: " + app.getConfig().getSelfAddress().address());
			System.out.println("################################################\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
