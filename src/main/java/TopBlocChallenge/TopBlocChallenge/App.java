package TopBlocChallenge.TopBlocChallenge;


import org.apache.http.entity.StringEntity;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.IOException;

/**
 * Top Bloc Challenge
 *
 */
public class App 
{
	//values do not need to be updated
	static final String url = "http://34.239.125.159:5000//challenge"; 
	static final String id = "madisonrpotter@gmail.com";
	
	//main creates the data structures to store values and also calls helper functions
	public static void main( String[] args ) 
	{
		File input1 = new File("/Users/madison/TopBlocChallenge/TopBlocChallenge/src/ExcelFiles/Data1.xlsx");
		File input2 = new File("/Users/madison/TopBlocChallenge/TopBlocChallenge/src/ExcelFiles/Data2.xlsx");
		ArrayList <Integer> numberSetOneSheet1 =  new ArrayList<Integer>(); 	
		ArrayList <Integer> numberSetTwoSheet1 =  new ArrayList<Integer>(); 	
		ArrayList <String> wordSetOneSheet1 =  new ArrayList<String>(); 	
		ArrayList <Integer> numberSetOneSheet2 =  new ArrayList<Integer>(); 	
		ArrayList <Integer> numberSetTwoSheet2 =  new ArrayList<Integer>(); 	
		ArrayList <String> wordSetOneSheet2 =  new ArrayList<String>(); 	
		readinWorkbook(input1, numberSetOneSheet1, numberSetTwoSheet1, wordSetOneSheet1);
		readinWorkbook(input2, numberSetOneSheet2, numberSetTwoSheet2, wordSetOneSheet2);
		int [] numberSetOne = multiply(numberSetOneSheet1,numberSetOneSheet2);
		int [] numberSetTwo = divide(numberSetTwoSheet1,numberSetTwoSheet2);
		String [] wordSetOne = concatenate(wordSetOneSheet1, wordSetOneSheet2);
		JSONObject json = createJson(id, numberSetOne, numberSetTwo, wordSetOne);
		post(json, url); 
	}

	//performs multiplication on the columns 
	public static int [] multiply(ArrayList <Integer>  a, ArrayList <Integer>  b)  
	{    
		int multiplySet[] = new int [a.size()];
		for (int i=0; i <a.size(); i++) {
			multiplySet[i] = a.get(i)* b.get(i);     

		}
		return multiplySet;
	}
	
	//performs the division on the columns
	public static int [] divide(ArrayList <Integer>  a, ArrayList <Integer>  b)  
	{    
		int divideSet[] = new int [a.size()];
		for (int i=0; i <a.size(); i++) {
			divideSet[i] = a.get(i)/b.get(i);     
		}
		return divideSet;
	}
	
	//concatenates the stored columns of words and returns array of concatenated words
	public static String [] concatenate(ArrayList <String>  a, ArrayList <String>  b)  
	{    
		String concatWordSet[] = new String [a.size()];
		for (int i=0; i <a.size(); i++) {
			concatWordSet[i] = a.get(i).concat(" ").concat(b.get(i));     
		}
		return concatWordSet;
	}
	
	//Reads in a Workbook and stores the information into data corresponding data structures based on expected formatting
	static void readinWorkbook (File f,ArrayList <Integer> m, ArrayList <Integer> d, ArrayList <String> w) {
		try {
			InputStream fs = new FileInputStream(f);   
			System.out.print("FileInputStream" + fs);
			Workbook workbook  = new XSSFWorkbook(fs);

			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = sheet.iterator();
			int row = 1; //skip the header row
			while (iterator.hasNext()) {
				Row nextRow = iterator.next();
				Iterator<Cell> cellIterator = nextRow.cellIterator(); 
				while (cellIterator.hasNext()) { //goes through columns
					Cell cell = cellIterator.next();			
					if (row++==1) {
						break;	
					}
					switch (cell.getCellTypeEnum()) {
					case NUMERIC:
						m.add((int) cell.getNumericCellValue());
						cell = cellIterator.next();	
						d.add((int) cell.getNumericCellValue());
						break;
					case STRING:
						w.add(cell.getStringCellValue());
						break;
					default:
						System.out.println("Error incorrect cell type");
						break;
					}
				}
			}
			workbook.close();
			fs.close();		
		}
		catch ( FileNotFoundException fe) {
			System.out.println("File Not Found!");
		}
		catch (IOException oe) {
			System.out.println("IOException!");
		}

	}
	
	//Creates the JSON Object
	private static JSONObject createJson(String id, int [] numberSetOne, int [] numberSetTwo, String [] wordSetOne)  {
		JSONObject json = new JSONObject();
		try {
			json.put("id",id);
			json.put("numberSetOne",numberSetOne);
			json.put("numberSetTwo",numberSetTwo);
			json.put("wordSetOne",wordSetOne);	
		} catch (JSONException e) {
			System.out.println("JSON Exception! " + e);
		}
		return json;
	}

	//POSTS the JSON Request 
	private static void post (JSONObject json, String url) {
		try {
			StringEntity entity = new StringEntity(json.toString());
			HttpClient client= new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(entity);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");
			HttpResponse resp = client.execute(httpPost);
			System.out.println(json.toString() + "Response " + resp.getStatusLine());
		}
		catch (IOException ie) {
			System.out.println("IOException! " + ie);

		} 

	}

}



