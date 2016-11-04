package threadPool;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class ThreadPoolTest {

	public static void main(String[] args)
	{
		Scanner in = new Scanner(System.in);
		System.out.print("Enter directory:");
		String directory = in.nextLine();
		System.out.print("Enter keyWord:");
		String keyWord = in.nextLine();
		
		ExecutorService pool = Executors.newCachedThreadPool();
		MatchCounter counter = new MatchCounter(new File(directory),keyWord,pool);
		Future<Integer> result = pool.submit(counter);
		
		try
		{
			System.out.println(result.get() + " match files.");
			
		}
		catch(ExecutionException e)
		{
			e.printStackTrace();
		}
		catch(InterruptedException e)
		{
			
		}
		
		pool.shutdown();
		int largestPoolSize = ((ThreadPoolExecutor) pool).getLargestPoolSize();
		System.out.println("largest pool size = "+ largestPoolSize);
	}
}


class MatchCounter implements Callable<Integer>
{
	private File directory;
	private String key;
	private ExecutorService pool;
	private int count;
	
	public MatchCounter(File d, String k, ExecutorService p)
	{
		this.directory = d;
		this.key = k;
		this.pool = p;
	}
	
	public Integer call()
	{
		count = 0;
		
		try
		{
			File[] files = directory.listFiles();
			List<Future<Integer>> results = new ArrayList<>();
			
			for(File file: files)
			{
				if(file.isDirectory())
				{
					MatchCounter counter = new MatchCounter(file,key,pool);
					Future<Integer> result = pool.submit(counter);
					results.add(result);
				}
				else
				{
					if(search(file))
						count++;
				}
			}
			
			for(Future<Integer> result: results)
			{
				try
				{
					count += result.get();
					
				}
				catch(ExecutionException e)
				{
					
				}
			}
		}
		catch(InterruptedException e)
		{
			
		}
		
		return count;
	}
	
	public boolean search(File file)
	{
		
		try{
			Scanner in = new Scanner(file);
			boolean found = false;
			while(!found && in.hasNextLine())
			{
				String line = in.nextLine();
				if(line.contains(key))
					found = true;
			}
			in.close();
			return found;
		}
		catch(IOException e)
		{
			return false;
		}
	}
	
}