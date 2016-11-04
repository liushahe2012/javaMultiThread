package future;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class FutureTest {
	public static void main(String[] args)
	{
		Scanner in = new Scanner(System.in);
		System.out.print("Enter directory:");
		String directory = in.nextLine();
		System.out.print("Enter keyword:");
		String keyWord = in.nextLine();
		
		MatchCounter counter = new MatchCounter(new File(directory), keyWord);
		FutureTask<Integer> task = new FutureTask<>(counter);
		Thread t = new Thread(task);
		t.start();
		
		try{
			System.out.println(task.get() + " match files");
		}
		catch(ExecutionException e)
		{
			e.printStackTrace();
		}
		catch(InterruptedException e)
		{
			
		}
		
	}
}


class MatchCounter implements Callable<Integer>
{
	private File directory;
	private String keyWord;
	private int count;
	
	public MatchCounter(File d, String s)
	{
		this.directory = d;
		this.keyWord = s;
	}
	public Integer call()
	{
		count = 0;
		try
		{
			File[] files = directory.listFiles();
			ArrayList<Future<Integer>>  results = new ArrayList<>();
			
			for(File file: files)
			{
				if(file.isDirectory())
				{
					MatchCounter counter = new MatchCounter(file,keyWord);
					FutureTask<Integer> task = new FutureTask<>(counter);
					results.add(task);
					Thread t = new Thread(task);
					t.start();
				}
				else{
					if(search(file))
						count ++;
				}
				
				for(Future<Integer> result : results)
				{
				
					try
					{
						count += result.get();
					}
					catch(ExecutionException e)
					{
						e.printStackTrace();
					}
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
		try
		{
			Scanner in = new Scanner(file);
			boolean found = false;
			while(!found && in.hasNextLine())
			{
				String line = in.nextLine();
				if(line.contains(keyWord))
					found = true;
			}
			return found;
		}
		catch(IOException e)
		{
			return false;
		}
	}
}

