/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OS_Ass1_CLI;

/**
 *
 * @author Omar Mostafa
 */


import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;


public class Terminal {
    	private static ArrayList<String> listOfCommands = new ArrayList<String>();
    public static void initializeListOfCommands() 
	{
		listOfCommands.add("clear");
		listOfCommands.add("cd");
		listOfCommands.add("cp");
		listOfCommands.add("mkdir");
		listOfCommands.add("ls");
		listOfCommands.add("pwd");
		listOfCommands.add("mv");
		listOfCommands.add("rm");
		listOfCommands.add("cat");
		listOfCommands.add("more");
		listOfCommands.add("rmdir");
                
                /* functions removed due to unusability 
                        listOfCommands.add("less");
                        listOfCommands.add("man ls"); 
                        listOfCommands.add("grep");
                */
                
		listOfCommands.add("help");
		listOfCommands.add("args");
		listOfCommands.add("date");
		listOfCommands.add("exit");
	}
 
    
        
	
	public static boolean validate(String command) 
	{
                return listOfCommands.contains(command);
	}

	public static File changeDirectory(String path, String curr) 
	{
		File x = new File(curr);
		for(int i=0;i<x.listFiles().length;i++) 
		{
			if(x.listFiles()[i].getName().equals(path))
				return x.listFiles()[i];
		}
		File temp = new File(path);
		if(!temp.exists()) 
		{
			temp = new File(curr);
		}
		return temp;
	}

	public static void pwd(File f) 
	{
		System.out.println(f.getAbsolutePath());
	}
	
	public static void terminate() 
	{
		Date date = new Date() ;
		System.out.println("Program terminated on "+date.toString());
	}
	
	public static void listAllFiles(String currentDirectory, boolean showHidden)
	{
		File f = new File(currentDirectory) ;
		File arr[] = f.listFiles() ;
		Arrays.sort(arr);
		if(showHidden==true)
		{
			for(int i=0;i<arr.length;i++)
			{
				if(arr[i].isHidden())
					System.out.print(".");
				System.out.print(arr[i].getName()+" ");
				if(i%4==0)
					System.out.println();
			}
			System.out.println();
		}else
		{
			for(int i=0, j=0;i<arr.length;i++)
			{
				if(!arr[i].isHidden())
				{
					System.out.print(arr[i].getName()+" ");
					j++ ;
				}
				if(j%4==0&&!arr[i].isHidden())
					System.out.println();
			}
			System.out.println();
		}
	}
	
	public static void copyFile(File arr[], String command) throws IOException
	{
		int l = command.lastIndexOf(' ') ;
		if(l==-1)
		{
			System.out.println("Not enough arguments for cp");
			return ;
		}
		String lastArg = command.substring(l+1, command.length()) ;
		String checker = lookup(arr, lastArg) ;
		if(checker.equals("Not found"))
		{
			System.out.println("Error: destination not found");
			return ;
		}
		File dest = new File(checker) ;
		if(dest.isDirectory()) // if its is a directory then copy all other arguments(files) into it
		{
			for(int i=0,j=0;i<=l;i++)
			{
				if(command.charAt(i)==' ') // loop on all other arguments
				{
					String arg = command.substring(j, i) ;
					checker = lookup(arr, arg) ;
					File f = new File(checker) ;
					if(!f.exists())
					{
						System.out.println("Error: no such file named "+arg);
						break ;
					}else if(f.isDirectory())
					{
						System.out.println("Error: can't copy a directory using cp "+arg);
						break ;
					}
					//System.out.println(f.getAbsolutePath()+" "+dest.getAbsolutePath());
					Files.copy(f.toPath(), dest.toPath().resolve(f.toPath().getFileName()), StandardCopyOption.REPLACE_EXISTING);
					j = i+1 ;
				}
			}
		}else if(command.indexOf(' ')==l) // only two arguments given
		{
			String arg = command.substring(0, l) ;
			checker = lookup(arr, arg) ;
			if(checker.equals("Not found"))
			{
				System.out.println("Error: no such file named "+arg);
				return ;
			}
			File f = new File(checker) ;
			InputStream in = new FileInputStream(f);
			OutputStream out = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int len;
			while((len=in.read(buf))>0) 
			{
				out.write(buf, 0, len); //(bytes array, starting offset, length)
			}
			in.close();
			out.close();
		}
	}
	
	public static void moveFile(File arr[], String command) throws IOException
	{
		int l = command.lastIndexOf(' ') ;
		if(l==-1)
		{
			System.out.println("Not enough arguments for mv");
			return ;
		}
		String lastArg = command.substring(l+1, command.length()) ;
		String checker = lookup(arr, lastArg) ;
		if(checker.equals("Not found"))
		{
			System.out.println("Error: destination not found");
			return ;
		}
		File dest = new File(checker) ;
		if(dest.isDirectory())
		{
			for(int i=0,j=0;i<=l;i++)
			{
				if(command.charAt(i)==' ')
				{
					String arg = command.substring(j, i) ;
					checker = lookup(arr, arg) ;
					File f = new File(checker) ;
					if(!f.exists())
					{
						System.out.println("Error: no such file named "+arg);
						break ;
					}else if(f.isDirectory())
					{
						System.out.println("Error: can't move a directory using mv "+arg);
						break ;
					}
					//System.out.println(f.getAbsolutePath()+" "+dest.getAbsolutePath());
					Files.move(f.toPath(), dest.toPath().resolve(f.toPath().getFileName()), StandardCopyOption.REPLACE_EXISTING);
					j = i+1 ;
				}
			}
		}else if(command.indexOf(' ')==l) // only two arguments are given
		{
			String arg = command.substring(0, l) ;
			checker = lookup(arr, arg) ;
			if(checker.equals("Not found"))
			{
				System.out.println("Error: no such file named "+arg);
				return ;
			}
			File f = new File(checker) ;
			InputStream in = new FileInputStream(f);
			OutputStream out = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) 
			{
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			f.delete() ;
		}
	}
	
	static String lookup(File arr[], String fileName)
	{
		for(int i=0;i<arr.length;i++)
		{
			if(arr[i].getName().equals(fileName))
				return arr[i].getAbsolutePath() ;
		}
		return "Not found" ;
	}
	
	static void clearConsole()
	{
		for(int i=0;i<7;i++) 
		{
		    System.out.println() ;
		}
	}
	
	static void cat(File arr[], String fileName)
	{
		String absPath = lookup(arr, fileName) ;
		if(absPath.equals("Not found"))
		{
			System.out.println("No such file named: "+fileName);
			return ;
		}
		File f = new File(absPath) ;
		try {
			Scanner input = new Scanner(f) ;
			while(input.hasNextLine())
			{
				System.out.println(input.nextLine());
			}
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	static String concatenate(File arr[], String file1Name, String file2Name)
	{
		String absPath1 = lookup(arr, file1Name), absPath2 = lookup(arr, file2Name) ;
		if(absPath1.equals("Not found"))
		{
			System.out.println("No such file named: "+file1Name);
			return file2Name ;
		}else if(absPath2.equals("Not found"))
		{
			System.out.println("No such file named: "+file2Name);
			return file2Name ;
		}
		File f1 = new File(absPath1) ;
		File f2 = new File(absPath2) ;
		StringBuilder file1Content = new StringBuilder() ;
		try {
			Scanner input = new Scanner(f1) ;
			while(input.hasNextLine())
			{
				file1Content.append(input.nextLine());
				file1Content.append("\n") ;
			}
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			Files.write(f2.toPath(), file1Content.toString().getBytes(), StandardOpenOption.APPEND) ;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file2Name ;
	}
	
	static void displayHelp()
	{
		System.out.println("date: Displays system date and time");
		System.out.println("help: List all commands and functionalities");
		System.out.println("args: List all commands arguments");
		System.out.println("clear: Clears the console");
		System.out.println("cd: Changes current working directory");
		System.out.println("ls: List all contents of current directory");
//		System.out.println("man ls: Displays possible arguments and how to deal with command ls");
		System.out.println("pwd: Displays the absolute path of current directory");
		System.out.println("cp: Copies files");
		System.out.println("mv: Moves files");
		System.out.println("mkdir: Creates a new directory");
		System.out.println("rmdir: Deletes a directory");
		System.out.println("rm: Deletes a file");
		System.out.println("cat: Displays contents of a file and concatenates files and display output");
		System.out.println("more: Let us display and scroll down the output in one direction only");
//		System.out.println("less: Like more but more enhanced");
		System.out.println("exit: Terminates the program");
	}
	
	static void displayArgs()
	{
		System.out.println("cd: [arg] changes working directory to the given arg");
		System.out.println("cd: [no arg] changes working directory to current directory");
		System.out.println("ls: [no arg] displays contents of a file");
		System.out.println("ls: [-a] displays contents of a file including hidden files");
		System.out.println("cp: [arg1] [arg2] copies contents of arg1(file) to arg2(file)");
		System.out.println("cp: [arg1] [arg2] [argN] copies all given arguments from arg1->argN-1 to"
				+ "directory argN");
		System.out.println("mv: [arg1] [arg2] copies contents of arg1(file) to arg2(file) and deletes arg1");
		System.out.println("cp: [arg1] [arg2] [argN] moves all given arguments from arg1->argN-1 to"
				+ "directory argN");
		System.out.println("mkdir: [arg] creates a directory with whose name is the given argument");
		System.out.println("rmdir: [arg] deletes a directory whose name is given argument");
		System.out.println("rm: [arg] deletes a file whose name is the given argument");
		System.out.println("cat: [arg1] displays contents of arg1(file)");
		System.out.println("cat: [arg1] [arg2] concatenates contents of arg1 to contents of arg2 and"
				+ "displays the result");
	}
        
        
	public static void more(String path, String curr) {
		File f = new File(curr + "\\" + path);
		if (f.exists()) {
			try {
				FileInputStream a = new FileInputStream(f);
				BufferedReader br = new BufferedReader(new InputStreamReader(a));
				String l;
				int c = 0;
				int x;
				Scanner in = new Scanner(System.in);
				while ((l = br.readLine()) != null) {
					System.out.println(l);
					c++;
					if (c % 10 == 0) {
						System.out
								.print(".................................MORE press 1 quit press 2 ");
						x = in.nextInt();
						if (x == 2)
							break;
					}
				}
				br.close();
				in.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		f = new File(path);
		if (f.exists()) {
			try {
				FileInputStream a = new FileInputStream(f);
				BufferedReader br = new BufferedReader(new InputStreamReader(a));
				String l;
				int c = 0;
				int x;
				Scanner in = new Scanner(System.in);
				while ((l = br.readLine()) != null) {
					System.out.println(l);
					c++;
					if (c % 10 == 0) {
						System.out
								.print(".................................MORE press 1 quit press 2 ");
						x = in.nextInt();
						if (x == 2)
							break;
					}
				}
				
				br.close();
				in.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
	}
	
	public static boolean rm(String path, String curr) {
		String[] command = path.split("\\s+");
		if (command.length > 2) {
			return false;
		}
		if (command.length == 1) {
			File a = new File(curr + "\\" + path);
			if (!a.isDirectory() && a.exists())
				return a.delete();
			a = new File(path);
			if (!a.isDirectory())
				return a.delete();
		}
		if (command.length == 2) {
			if (command[0].equals("-d")) {
				File z = new File(curr + "\\" + command[1]);
				// System.out.println(z.getAbsolutePath());
				if (z.isDirectory()) {
					return z.delete();
				}
				z = new File(command[1]);
				// System.out.println(z.getAbsolutePath());
				if (z.isDirectory())
					return z.delete();
			} /*
			 * else if (command[0].equals("-r")) {
			 * 
			 * }
			 */
		}
		return false;
	}
	
	public static boolean makeDir(String pathOrname, String curr) {
		File f = new File(curr + "\\" + pathOrname);
		// System.out.println(curr + "\\" + pathOrname);
		boolean flag = f.mkdir();
		if (flag)
			return flag;
		File f2 = new File(pathOrname);
		boolean flag2 = f2.mkdir();
		if (flag2)
			return flag2;
		return false;
	}
    }



