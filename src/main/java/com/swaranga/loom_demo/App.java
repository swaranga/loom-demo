package com.swaranga.loom_demo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.glassfish.jersey.servlet.ServletContainer;

public class App {
	public static void main(String[] args) throws Exception {
		PoolType poolType = PoolType.valueOf(args[0]);

		System.out.println("Using pooltype: " + poolType);

		Server server = new Server(getPool(poolType));
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(Integer.parseInt(args[1]));

		server.addConnector(connector);

		ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);

		ctx.setContextPath("/");
		server.setHandler(ctx);

		ServletHolder serHol = ctx.addServlet(ServletContainer.class, "/rest/*");
		serHol.setInitOrder(1);
		serHol.setInitParameter("jersey.config.server.provider.packages", "com.amazon.loom_demo");

		try {
			System.out.println("Starting server");
			server.start();

			System.out.println("Server started");
			// server.join();

			// System.out.println("Server join exited");

			new CompletableFuture<String>().join();
			System.out.println("CompletableFuture join exited");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println("Exception: " + ex.getMessage());
			System.out.println("Exception: " + ex.getMessage());
		} finally {
			// server.destroy();
		}
	}

	private static ThreadPool getPool(PoolType poolType) {
		switch (poolType) {

		case fiber:
			return fiberPool();

		case thread:
			new QueuedThreadPool(100);

		case custom:
		default:
			return customPool();
		}
	}
	
	private static ThreadPool customPool() {
	    final ThreadPoolExecutor executor = new ThreadPoolExecutor(100, 100, 100, TimeUnit.SECONDS , new LinkedBlockingQueue<Runnable>());
	    executor.prestartAllCoreThreads();
	    
		return new ThreadPool() {
			public void execute(Runnable command) {
				System.out.println("Executing via custom pool");
				executor.execute(command);
			}

			public void join() throws InterruptedException {
				System.out.println("Sleeping forever");
				new CompletableFuture<String>().join();
			}

			public boolean isLowOnThreads() {
				System.out.println("isLowOnThreads");
				return false;
			}

			public int getThreads() {
				System.out.println("getThreads");
				return executor.getPoolSize();
			}

			public int getIdleThreads() {
				System.out.println("getIdleThreads");
				return executor.getPoolSize() - executor.getActiveCount();
			}
		};
	}

	private static ThreadPool fiberPool() {
		return new ThreadPool() {
			public void execute(Runnable command) {
				System.out.println("Executing via Fiber");
				try {
					java.lang.Fiber.schedule(() -> {
						System.out.println("Executing INSIDE Fiber");
						command.run();
					    System.out.println("Executed INSIDE Fiber");
					});
				} catch(Throwable t) {
					System.out.println("Exception: " + t.getMessage());
				}
			}

			public void join() throws InterruptedException {
				System.out.println("Sleeping forever");
				new CompletableFuture<String>().join();
			}

			public boolean isLowOnThreads() {
				System.out.println("isLowOnThreads");
				return false;
			}

			public int getThreads() {
				System.out.println("getThreads");
				return Integer.MAX_VALUE;
			}

			public int getIdleThreads() {
				System.out.println("getIdleThreads");
				return Integer.MAX_VALUE;
			}
		};
	}
}
