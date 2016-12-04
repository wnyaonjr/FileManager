import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	static final String COMMAND_DEL = "del";
	static final String COMMAND_RENAME = "rename";
	static final String COMMAND_MOVE = "move";
	static final String COMMAND_COPY = "copy";
	
	
	public static void main(String[] args) throws IOException {
		
		dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mma");
		String command = "";
		Scanner inputReader = new Scanner(System.in);
		String currDir = System.getProperty("user.dir");
		
		do
		{
			System.out.print(currDir+"> ");
			//TODO get current directory location
			 command = inputReader.nextLine();
			 
			 String commandParts[] = command.split(" ");
			 int commandLength = commandParts.length;
			 
			 if(commandParts[0].equals(COMMAND_DIR)) {
				 displayFilesAndFolder(currDir);
			 } else if (commandParts[0].equals(COMMAND_EXIT)) {
				
			 } else if (commandParts[0].equals(COMMAND_DEL)) {
				 
				 if(commandLength > 1){
					 try 
					 {		Files.delete(Paths.get(currDir+"\\" + commandParts[1]));
							System.out.println("Sucessful Delete: "+ commandParts[1]);
					 
					 } catch (IOException e) {
						e.printStackTrace();
					 }
				 }
				 else{
					 	System.out.println("del <targetFile>");
				 }
				 
			 }else if(commandParts[0].equals(COMMAND_RENAME)){
				 
				 if(commandLength > 1){
					 
					 File oldFileName = new File(currDir + "\\" + commandParts[1]);
					 File newFileName = new File(currDir + "\\" + commandParts[2]);
					  
					 if (oldFileName.renameTo(newFileName)) {
						  System.out.println("File renamed successfull !");
					 } else {
						  System.out.println("File rename operation failed !");
					 }
				 }
				 else{
					 	System.out.println("rename <oldFile>.<extension> <newFile>.<extension>");
				 }
				 	 
			 }else if(commandParts[0].equals(COMMAND_MOVE)){
				 
				    InputStream inStream = null;
					OutputStream outStream = null;
					
					File oldFile = new File(commandParts[1]);
			    	File newFile = new File(commandParts[2]);

			    	    inStream = new FileInputStream(oldFile);
			    	    outStream = new FileOutputStream(newFile);

			    	    byte[] buffer = new byte[1024];

			    	    int length;
			    	    //copy the file content in bytes
			    	    while ((length = inStream.read(buffer)) > 0){

			    	    	outStream.write(buffer, 0, length);

			    	    }

			    	    inStream.close();
			    	    outStream.close();

			    	    //delete the original file
			    	    oldFile.delete();

			    	    System.out.println("File is moved successful!");
				 
			 }
			 else if(commandParts[0].equals(COMMAND_COPY)){
				 
				    InputStream inStream = null;
					OutputStream outStream = null;
					
					File oldFile = new File(commandParts[1]);
			    	File newFile = new File(commandParts[2]);

			    	    inStream = new FileInputStream(oldFile);
			    	    outStream = new FileOutputStream(newFile);

			    	    byte[] buffer = new byte[1024];

			    	    int length;
			    	    //copy the file content in bytes
			    	    while ((length = inStream.read(buffer)) > 0){

			    	    	outStream.write(buffer, 0, length);

			    	    }

			    	    inStream.close();
			    	    outStream.close();


			    	    System.out.println("File is copied successful!");
				 
			 }
			 else {
				 if(!command.isEmpty()){
					 System.out.println("command not supported.");
				 }
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
	    
	    System.out.println("Total Space: "+ readableFileSize(totalSpace));
	    System.out.println("Free Space: "+ readableFileSize(freeSpace));
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
