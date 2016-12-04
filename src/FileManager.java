import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.UserPrincipal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class FileManager {

	static SimpleDateFormat dateFormatter = null;
	static final String COMMAND_DIR = "dir";
	static final String COMMAND_EXIT = "exit";
	
	
	public static void main(String[] args) {
		
		dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mma");
		String command = "";
		Scanner inputReader = new Scanner(System.in);
		String currDir = System.getProperty("user.dir");
		
		do
		{
			System.out.print(currDir+"> ");
			//TODO get current directory lWocation
			 command = inputReader.nextLine();
			 if(command.equals(COMMAND_DIR)) {
				 displayFilesAndFolder(currDir);
			 } else if (command.equals(COMMAND_EXIT)) {
				
			 } else {
				 System.out.println("command not supported.");
			 }
			
		}while(!command.equals(COMMAND_EXIT));
		
		inputReader.close();
	}
	
	/**
	 * print files and directories inside a given path
	 * @param currDir
	 */
	private static void displayFilesAndFolder(String currDir) {
		File folder = new File(currDir);
		File[] listOfFiles = folder.listFiles();

		System.out.println("Date Created\t\tDate Modified\t\tType\t\t\tOwner\t\tSize\t\tPercent\tFile Name");
		long freeSpace = -1;
		long totalSpace = -1;
	    for (int i = 0; i < listOfFiles.length; i++) {
	    	if(freeSpace == -1)
	    	{
	    		freeSpace = listOfFiles[i].getFreeSpace();
	    		totalSpace = listOfFiles[i].getTotalSpace();
	    	}
	    	System.out.println(getFileDetails(listOfFiles[i]));
	    }
	    
	    System.out.println("Total Space: "+readableFileSize(totalSpace));
	    System.out.println("Free Space: "+readableFileSize(freeSpace));
	}
	
	private static String getFileDetails(File inputFile)
	{
		String fileDetails = "";
		try {
    		Path filePath = inputFile.toPath();
			BasicFileAttributes attr = Files.readAttributes(inputFile.toPath(), BasicFileAttributes.class);
			
			//date created
			fileDetails = getFileTimeString(attr.creationTime());
			
			//date modified
			fileDetails += "\t"+getFileTimeString(attr.lastModifiedTime());
			
			//file or directory
			if(inputFile.isDirectory()){
				fileDetails += "\t<DIR>";
			}else{
				fileDetails += "\t";
			}
			
			//owner
			FileOwnerAttributeView ownerAttributeView = Files.getFileAttributeView(filePath, FileOwnerAttributeView.class);
		    UserPrincipal owner = ownerAttributeView.getOwner();
			fileDetails += "\t"+owner;
			
			//file size
			fileDetails += "\t" + readableFileSize(attr.size());
			
			//percent occupied
			long totalSpace = inputFile.getTotalSpace();
			double percentUsed = (double) (((double)attr.size()/(double)totalSpace)*(double)100);
			fileDetails += "\t\t"+String.format("%.2f", percentUsed)+"%";
			
			//file name
			fileDetails += "\t" + inputFile.getName();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileDetails;
	}
	
	/**
	 * 
	 * @param fileTime
	 * @return
	 */
	private static String getFileTimeString(FileTime fileTime)
	{
		return dateFormatter.format(fileTime.toMillis());
	}
	
	/**
	 * 
	 * @param size
	 * @return
	 */
	public static String readableFileSize(long size) {
	    if(size <= 0) return "0";
	    final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
	    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
	    return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + "" + units[digitGroups];
	}
}
