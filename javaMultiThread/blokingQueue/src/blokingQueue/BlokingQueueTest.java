package blokingQueue;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class BlokingQueueTest
{
	public static void main(String[] args)
	{
		Scanner in = new Scanner(System.in);
		System.out.print("please input directory:");
		String directory = in.nextLine();
		System.out.print("please input keyword:");
		String keyword = in.nextLine();
		
		final int FILE_QUEUE_SIZE = 10;
		final int THREADS_NUMBER = 2;
		
		ArrayBlockingQueue<File> queue = new ArrayBlockingQueue<>(FILE_QUEUE_SIZE);
		FileEnumerationTask enumerate = new FileEnumerationTask(queue,new File(directory));
		new Thread(enumerate).start();
		
		for(int i=1; i < THREADS_NUMBER; i++)
		{
			new Thread(new SearchTask(queue,keyword)).start();
		}
		
	}
}

class FileEnumerationTask implements Runnable
{
	public static File DUMMY = new File("");
	private ArrayBlockingQueue<File> queue;
	private File directory;
	
	public FileEnumerationTask(ArrayBlockingQueue<File> queue, File d)
	{
		this.queue = queue;
		this.directory = d;
	}
	public void run()
	{
		try{
			enumerate(directory);
			queue.put(DUMMY);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	public void enumerate(File directory) throws InterruptedException
	{
		File[] files = directory.listFiles();
		for(File file : files)
		{
			if(file.isDirectory())
				enumerate(file);
			else
				queue.put(file);
		}
	}
}

class SearchTask implements Runnable
{
	private ArrayBlockingQueue<File> queue;
	private String keyword;
	
	public SearchTask(ArrayBlockingQueue<File> queue, String key)
	{
		this.queue = queue;
		this.keyword = key;
	}
	
	public void run()
	{
		try{
			boolean done = false;
			while(!done)
			{
				File file = queue.take();
				if(file == FileEnumerationTask.DUMMY)
				{
					done = true;
					queue.put(file);
				}
				else
				{
					search(file);
				}
			}
		}
		catch(IOException e)
		{
			
		}
		catch(InterruptedException e)
		{
			
		}
	}
	
	public void search(File file) throws IOException
	{
		Scanner in = new Scanner(file);
		
		int lineNumber = 0;
		while(in.hasNextLine())
		{
			lineNumber ++;
			String s = in.nextLine();
			if(s.contains(keyword))
			{
				System.out.println("keyword:" + keyword);
				System.out.printf("file:%s,line:%d,string:%s%n",file.getPath(),lineNumber,s);
			}
		}
		//in.close();
		
	
	}
}






