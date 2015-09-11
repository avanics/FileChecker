import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;


public class FileCheckerTest {

	private String fileDir ;
	private String fileDirName;
	private String location;
	public String getFileDir() {
		return fileDir;
	}
	public void setFileDir(String fileDir) {
		this.fileDir = fileDir;
	}
	public String getFileDirName() {
		return fileDirName;
	}
	public void setFileDirName(String fileDirName) {
		this.fileDirName = fileDirName;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FileCheckerTest ft = new FileCheckerTest();
		// TODO Auto-generated method stub
		if(ft.isDeviceIdCorrect())
			System.out.println("The device ID is correct");
		else
			System.out.println("The device ID is incorrect");
		ft.checkTimes();//This function checks the times present in the root directory and superblock 
		ArrayList<Integer> lstFinalBlockList = new ArrayList<Integer>();
		ArrayList<Integer> lstNonFreeBlocks = ft.checkFreeBlocks( 26 , lstFinalBlockList);
		for(int j =0 ; j < lstNonFreeBlocks.size(); j++)
		{
			System.out.println("List of Occupied Blocks :"+lstNonFreeBlocks.get(j));
		}
		Integer blockNo =0;
		/*This piece of code checks all blocks from fusedata.1 to fusedata.25 
		 * if any of the occupied blocks are present in the free block list or not and displays it*/
		for(int i=1; i < 26 ; i++)
		{
			try
			{
			StringBuffer fPath = new StringBuffer("/fusedata/fusedata.");
			fPath.append(i);
			String line ="";
			FileReader inputFile;
			inputFile = new FileReader(fPath.toString());
			BufferedReader bufferReader1  = new BufferedReader(inputFile);
			while ((line = bufferReader1.readLine()) != null)   {
				for( int l =0 ; l < lstNonFreeBlocks.size(); l++)
				{
					blockNo = lstNonFreeBlocks.get(l);
					if(line.contains((blockNo.toString())))
					System.out.println("The free block list  at block No " +i+" has an entry of block " +lstNonFreeBlocks.get(l)+ "  but the block is already occupied.Please remove it from the lists");
				}
			}
			}
			
		
			catch(Exception e )
			{
				e.printStackTrace();
			}
		}
		ft.checkLinkCount();//Function that checks the link count of the directory
		ft.checkIndirect();// Function that checks the indirect pointer
		/* Function that checks whether . and .. are present or not in a directory representation and 
		 * whether the current directory and parent directory block names are correct or not.*/
		ft.checkDirectoryStructure();
		ft.checkSize();
	}
public Boolean isDeviceIdCorrect()
{
	FileReader inputFile;
	try {
		inputFile = new FileReader("/fusedata/fusedata.0");//Path where all 10000 files will be stored
		 BufferedReader bufferReader = new BufferedReader(inputFile);
		    String line;
		    Boolean flag = false;
		    String devId;

   
		while ((line = bufferReader.readLine()) != null)   {
			StringTokenizer st = new StringTokenizer(line , ",:");
			while (st.hasMoreTokens()) {
				if(flag)
				{
					devId = st.nextToken();
					if(devId.equals("20"))
						return true;
					else return false;
				}
				if(st.nextToken().trim().equals("devId"))
					flag = true;
			}
		  //System.out.println(line);
		}
		return false;
}
		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return false;
}
public void checkTimes()
{
Long cTime = 0L;
FileReader inputFile;
try {
	System.out.println(System.currentTimeMillis()/1000);
	cTime = System.currentTimeMillis()/1000;//cTime represents the current time of the System
	/*To check the creation Time of Superblock*/
	inputFile = new FileReader("/fusedata/fusedata.0");
	 BufferedReader bufferReader = new BufferedReader(inputFile);

	    //Variable to hold the one line data
	    String line;
 
Boolean flag = false;
Integer creationTime = 0;

	while ((line = bufferReader.readLine()) != null)   {
		StringTokenizer st = new StringTokenizer(line , ",:");
		while (st.hasMoreTokens()) {
			if(flag)
			{
				String cTimeTemp = st.nextToken() ;
				if(cTimeTemp!= null)
				{
				creationTime = 	Integer.parseInt(cTimeTemp.trim());
				System.out.println(creationTime);
				}
				if(creationTime > cTime )
				{
					System.out.println("The time is in future , change it to present");
					flag = false;
					break;
				}
			
			}
			if(st.nextToken().trim().equals("{creationTime"))
				flag = true;
		}
	}
	/* Checking the atime , mtime and ctime of root directory */
	String cTimeTemp1 ="";
	inputFile = new FileReader("/fusedata/fusedata.26");
	BufferedReader bufferReader1 = new BufferedReader(inputFile);
	while ((line = bufferReader1.readLine()) != null)   {
		StringTokenizer st = new StringTokenizer(line , ",:");
		while (st.hasMoreTokens()) {
		 cTimeTemp1 = st.nextToken();
		 if(cTimeTemp1!= null)
		 {
			if(cTimeTemp1.trim().equals("ctime"))
			{	
				String cTimeValue = st.nextToken();
				if(cTimeValue!= null)
				creationTime = 	Integer.parseInt(cTimeValue.trim());
				if(creationTime > cTime )
				{
					System.out.println("The ctime is in future , change it to present");
				}
				
			}
			if(cTimeTemp1.trim().equals("atime"))
			{
				String aTimeValue = st.nextToken();
				if(aTimeValue!= null)
				creationTime = 	Integer.parseInt(aTimeValue.trim());
				if(creationTime > cTime )
				{
					System.out.println("The atime is in future , change it to present");
				}
				
			}	
			if(cTimeTemp1.trim().equals("mtime"))
			{
				String mTimeValue = st.nextToken();
				if(mTimeValue!= null)
				creationTime = 	Integer.parseInt(mTimeValue.trim());
				if(creationTime > cTime )
				{
					System.out.println("The mtime is in future , change it to present");
					break;
				}
			}
		 	}
		}
}
	
		
	
}
catch(Exception e)
{
	e.printStackTrace();
}
}
public void checkDirectoryStructure()
{
	String line ="";
	String fileNameInodeDic ="";
	String parentBlockInode = "";
	String blockNo ="";
	String parentBlockNo = "";
	Integer block=0;
	Boolean flag = false;
	try {
	for ( int i =26; i < 100 ; i++)/*Checking all the files from 26 upto 10000 for the valid directory structure*/
	{
		StringBuffer fPath = new StringBuffer("/fusedata/fusedata.");
		fPath.append(i);
		FileReader inputFile;
		
			inputFile = new FileReader(fPath.toString());
			BufferedReader bufferReader1  = new BufferedReader(inputFile);
			while ((line = bufferReader1.readLine()) != null)   {
			if(line.contains("filename_to_inode_dict"))	
			{
				StringTokenizer st = new StringTokenizer(line , "{");
				while (st.hasMoreTokens()) {
					String dir = st.nextToken();
					if(dir!= null)
					{
						fileNameInodeDic = st.nextToken();	
					}
				}
				/* Checking the occurence of '.' and '..' */
				if(! (fileNameInodeDic.contains(".")&& fileNameInodeDic.contains("..")))
				{
					System.out.println("The directory "+i+ "doesnot contain . and .. ");
				}
				/* Checking if the current directory name is correctly listed */
				String arr[] = fileNameInodeDic.split("[//:},]");
				for(int j= 0; j < arr.length ; j++)
				{
					/* Checking if the current directory name is correctly listed */
					if((arr[j].trim().equalsIgnoreCase(".")))
					{
						blockNo = arr[j+1];
						block = Integer.parseInt(blockNo);
						if(block != i)
						System.out.println("The block No is incorrect of the current directory "+i);
				    }
					/* Checking if the parent directory name is correctly listed */
					if((arr[j].trim().equalsIgnoreCase("..")))
					{
						parentBlockNo = arr[j+1];
						String lineRead = "";
						fPath = new StringBuffer("/fusedata/fusedata.");
						fPath.append(parentBlockNo);
/* Opening the parent directory block as mentioned in the block to check if it has an entry of the child directory in its file_name_to_inode_dictionary */
						inputFile = new FileReader(fPath.toString());
						bufferReader1  = new BufferedReader(inputFile);
						while ((lineRead = bufferReader1.readLine()) != null)   {
							if(lineRead.contains("filename_to_inode_dict"))	
							{
								 st = new StringTokenizer(lineRead , "{");
								 while (st.hasMoreTokens()) {
										String dir = st.nextToken();
										if(dir!= null)
										{
											parentBlockInode = st.nextToken();
											if(!parentBlockInode.contains(blockNo))
											{
												System.out.println("The parent directory "+blockNo+" mentioned is incorrect");
											}
											else
											{ 
												String arrParent[] = fileNameInodeDic.split("[//:},]");
												for(int k= 0; k < arr.length ; k++)
												{
													/* Checking if the current directory name is correctly listed */
													if((arr[k].trim().equalsIgnoreCase("d") && ((arr[k+1].trim().equalsIgnoreCase(blockNo)))))
													{
														System.out.println("The block No is correct of the parent directory ");
														flag = true;
														break;
												    }
													
												}
												if(!flag)
												{
													System.out.println("The block No of the parent directory "+parentBlockNo+" is incorrect for directory "+blockNo);	
												}
										}
								 	}
								}
						
						
							}
							else
							{
								System.out.println("The block No of the parent directory "+parentBlockNo+" is incorrect for directory "+blockNo);		
							}
						}
					}
	}
	}
	}
	}
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
}
public void checkLinkCount()
{
	String line ="";
	Integer lnCnt = 0;
	String fileNameInodeDic ="";
	try {
	for ( int i =26; i < 100 ; i++)/*Checking all the files from 26 upto 10000 for the valid linkcount condition*/
	{
		StringBuffer fPath = new StringBuffer("/fusedata/fusedata.");
		fPath.append(i);
		FileReader inputFile;
		
			inputFile = new FileReader(fPath.toString());
			BufferedReader bufferReader1  = new BufferedReader(inputFile);
			while ((line = bufferReader1.readLine()) != null)   {
			if(line.contains("filename_to_inode_dict"))	
			{
				StringTokenizer st = new StringTokenizer(line , ":,");
				while (st.hasMoreTokens()) {
					String linkCount = st.nextToken();
					if(linkCount!= null && linkCount.contains("linkcount"))
					{
						linkCount = st.nextToken();
					if(linkCount!= null)
					 lnCnt = Integer.parseInt(linkCount);
					break;
					}
				}
				 st = new StringTokenizer(line , "{");
				while (st.hasMoreTokens()) {
					String dirCheck = st.nextToken();
					if(dirCheck != null)
					{
							fileNameInodeDic = st.nextToken();
							break;
					}
					}
				Integer charCount =0;
				/*Checking the total no of links by counting the no of times a file or directory is present in file name to inode dictionary */
				for(int k =0 ; k<fileNameInodeDic.length(); k++){
			        if(fileNameInodeDic.charAt(k) == 'd' || fileNameInodeDic.charAt(k) == 'f'){ 
			            charCount++;
			        }
			    }
				if(!lnCnt.equals(charCount))/* Linkcount value in the directory is compared with the no of times d and f appears in the file_name_to_inode_dic */
				System.out.println("The linkcount is incorrect for file fusedata."+i);	
				}
			}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
public void checkIndirect()
{
	String line ="";
	Integer locn = 0;
	Integer indirect = 0;
	Boolean indirectFlag = false;
	String fileNameInodeDic ="";
	try {
	for ( int i =26; i < 100 ; i++)/*Checking all the files from 26 upto 10000 */
	{
		StringBuffer fPath = new StringBuffer("/fusedata/fusedata.");//Path where fusedata files are stored
		fPath.append(i);
		FileReader inputFile;
		
			inputFile = new FileReader(fPath.toString());
			BufferedReader bufferReader1  = new BufferedReader(inputFile);
			while ((line = bufferReader1.readLine()) != null)   {
				if(line.contains("location") && line.contains("indirect"))	
				{
					StringTokenizer st = new StringTokenizer(line , ":,}");
					while (st.hasMoreTokens()) {
						String location = st.nextToken();
						if(location!= null && location.contains("indirect"))
						{
							location = st.nextToken();
							if(location!= null)
								indirect = Integer.parseInt(location);		
						}
						if(location!= null && location.contains("location"))
						{
							location = st.nextToken();
							if(location!= null)
								locn = Integer.parseInt(location.trim());		
						}
						
				}
					/* Checking the block  mentioned in the location field */
					fPath = new StringBuffer("/fusedata/fusedata.");//path where fusedata files are stored
					fPath.append(locn);
					inputFile = new FileReader(fPath.toString());
					 bufferReader1  = new BufferedReader(inputFile);
					 while ((line = bufferReader1.readLine()) != null)   {
					if(line.contains(","))
						indirectFlag = true;
					else
						indirectFlag = false;
					 }
					 if(indirectFlag && indirect.equals(0))
					 System.out.println("the indirect pointer is incorrect for block fusedata."+i);
					 else if(!indirectFlag && indirect.equals(1))
					System.out.println("the indirect pointer is incorrect for block fusedata."+i);
			}
				
			}
			}
}
	catch(Exception e)
	{
		e.printStackTrace();
	}
}

public ArrayList<Integer> checkFreeBlocks(Integer fb , ArrayList <Integer> lstFinal)
{
	List nonFreeBlocks = new ArrayList<Integer>();
	String fileNameInodeDic ="";
	String line ="";
	/* Checking root directory for subsequent directories and files in order to determine the free block list*/
	try
	{
	StringBuffer fPath = new StringBuffer("/fusedata/fusedata.");
	fPath.append(fb.toString());
	FileReader inputFile = new FileReader(fPath.toString());
	BufferedReader bufferReader1  = new BufferedReader(inputFile);
	while ((line = bufferReader1.readLine()) != null)   {
		StringTokenizer st = new StringTokenizer(line , "{");
		while (st.hasMoreTokens()) {
			String dirCheck = st.nextToken();
			if(dirCheck != null)
			{
					fileNameInodeDic = st.nextToken();
					break;
				}
			}
		}
	int charCount =0;
	if(fileNameInodeDic!= null)
	{
	for(int i =0 ; i<fileNameInodeDic.length(); i++){
        if(fileNameInodeDic.charAt(i) == 'd'){
            charCount++;
        }
    }
	if(charCount <=2)
		return lstFinal;
	else
	{
		String arr[] = fileNameInodeDic.split("[//:},]");
		String blockNo ="";
		for (int i=0; i<arr.length ; i++)
		{
			/*Finding Directories */
		if((arr[i].trim().equalsIgnoreCase("d"))  && !(arr[i+1].trim().equalsIgnoreCase("..")))
		{
			blockNo = arr[i+2];
			if(!nonFreeBlocks.contains(Integer.parseInt(blockNo))&& Integer.parseInt(blockNo)!= 26)
			{
			nonFreeBlocks.add(Integer.parseInt(blockNo));
			lstFinal.add(Integer.parseInt(blockNo));
			}
		}
		/* Finding Files */
		if((arr[i].trim().equalsIgnoreCase("f")))
		{
			blockNo = arr[i+2];
			this.fileSearch (lstFinal , blockNo);/* Function Call to search blocks occupied by files */
		}
		}
		for(int k =0 ; k < nonFreeBlocks.size(); k++)
		{
			checkFreeBlocks((Integer)nonFreeBlocks.get(k) , lstFinal);//Recursively calling this method to find all directories
			
		}
		}
	}
		
}
	catch(Exception e)
	{
	e.printStackTrace();	
	}
	return lstFinal;
	}
/* Below Function searches for blocks occupied by files */
	public ArrayList<Integer>  fileSearch(ArrayList<Integer> lstFiles , String blockNo)
	{ 
		String indirect, location = "";
		Integer locn =0;
		try
		{
		StringBuffer fPath = new StringBuffer("/fusedata/fusedata.");
		fPath.append(blockNo);
		String line ="";
		if(!lstFiles.contains(Integer.parseInt(blockNo)))
		{
			lstFiles.add(Integer.parseInt(blockNo));
		}
		FileReader inputFile = new FileReader(fPath.toString());
		BufferedReader bufferReader1  = new BufferedReader(inputFile);
		while ((line = bufferReader1.readLine()) != null)   {
			StringTokenizer st = new StringTokenizer(line , ":,}");
			while (st.hasMoreTokens()) {
				 location = st.nextToken();
				
				if(location!= null && location.contains("location"))
				{
					location = st.nextToken();
					if(location!= null)
					{
						locn = Integer.parseInt(location.trim());
						lstFiles.add(locn);/*List of blocks occupied by files and its' inode structure */
					}
				}
				
		}
		}
		}
	catch(Exception e)
	{
	e.printStackTrace();	
	}
	return lstFiles;
	}
	/*Checking the size of the block  pointers in the location array based on the value of indirect flag */
	public void checkSize()
	{
		String line ="";
		Integer locn = 0;
		Integer indirect = 0;
		Boolean indirectFlag = false;
		String fileNameInodeDic ="";
		try {
		for ( int i =26; i < 31 ; i++)/*Checking all the files from 26 upto 10000 for checking the block size*/
		{
			StringBuffer fPath = new StringBuffer("/fusedata/fusedata.");//Path where fusedata files are stored
			fPath.append(i);
			FileReader inputFile;
			
				inputFile = new FileReader(fPath.toString());
				BufferedReader bufferReader1  = new BufferedReader(inputFile);
				while ((line = bufferReader1.readLine()) != null)   {
					if(line.contains("location") && line.contains("indirect"))	//Code to retreive all file inodes
					{
						StringTokenizer st = new StringTokenizer(line , ":,}");
						while (st.hasMoreTokens()) {
							String location = st.nextToken();
							if(location!= null && location.contains("indirect"))/*retreiving value  of indirect in file inode*/
							{
								location = st.nextToken();
								if(location!= null)
									indirect = Integer.parseInt(location);		
							}
							if(location!= null && location.contains("location"))/*retreiving value  of location in file inode*/
							{
								location = st.nextToken();
								if(location!= null)
									locn = Integer.parseInt(location.trim());		
							}
							if(indirect == 0)
							{
								 fPath = new StringBuffer("/fusedata/fusedata.");//Path where fusedata files are stored
								fPath.append(locn);
								 File file = new File(fPath.toString());
						        if(file.length() > 4096)
						        {
						        	System.out.println("The size of the location pointer is more than the block size when indirect flag = 0 ");
						        	break;
						        }
							    	
							}
							if(indirect == 1)
							{
								fPath = new StringBuffer("/fusedata/fusedata.");//Path where fusedata files are stored
								fPath.append(locn);
								inputFile = new FileReader(fPath.toString());
								 bufferReader1  = new BufferedReader(inputFile);
								while ((line = bufferReader1.readLine()) != null)   {
									String arr[] = line.split(",");/*Assuming index block is a file containing comma separated values*/
									for(int j = 0; j <arr.length; j ++)
									{
										 File file = new File(fPath.toString());
										 if(file.length() > 4096)
									        {
									        	System.out.println("The size of the each block of location pointer is more than the block size when indirect flag = 1 ");
									        	break;
									        }
									}
							}
							}
							
						}
					}
				}
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		}
		}

