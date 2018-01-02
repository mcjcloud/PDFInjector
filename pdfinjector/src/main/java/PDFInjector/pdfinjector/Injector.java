package PDFInjector.pdfinjector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonObject.Member;
import com.eclipsesource.json.JsonValue;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

public class Injector 
{	
	public static boolean inject(JsonObject data, File pdf, File output)
	{
		// inject data into output pdf
		try
		{
			// create pdf objects to manipulate
			PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdf), new PdfWriter(output));
			PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
			
			// create maps for successful and failed fields
			HashMap<String, String> failedFields = new HashMap<String, String>();
			HashMap<String, String> successFields = new HashMap<String, String>();
			
			// go through data, and try to populate each key in the PDF form
			Map<String, PdfFormField> formFields = form.getFormFields();	// map of fields in the PDF
			// iterate over the JSON object
			for(Member member : data)
			{
				String key = member.getName();
				JsonValue value = member.getValue();
				PdfFormField field = formFields.get(key);
				if(field == null)
				{
					// the field doesn't exist, add it to failedFields
					failedFields.put(key, value.toString());
				}
				else
				{
					// the field does exist, fill it
					successFields.put(key, value.toString());
					field.setValue(value.asString());
				}
			}
			pdfDoc.close();
			
			// forms have been filled, create the summary document
			String pathname = output.getParent();
			if(pathname != null)
			{
				try
				{
					File summaryDoc = new File(output.getParent() + "/summary.txt");
					summaryDoc.createNewFile();
					
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(summaryDoc)));
					
					// write successful forms
					bw.write("Filled Forms:\n");
					for(String key : successFields.keySet())
					{
						bw.write(key + " = " + successFields.get(key) + "\n");
					}
					bw.write("\n");
					
					// write failed fields
					bw.write("Failed Forms:\n");
					for(String key : failedFields.keySet())
					{
						bw.write(key + " = " + failedFields.get(key) + "\n");
					}
					bw.write("\n");
					bw.flush();
					bw.close();
				}
				catch(Exception e)
				{
					System.out.println("An error occurred writing the summary file. The output PDF was created.");
				}
			}
			else
			{
				System.out.println("Weird. The parent directory doesn't exist.");
				return false;
			}
			
		}
		catch(IOException ioe)
		{
			System.out.println("An error occurred opening the PDF stream.");
			return false;
		}
		
		
		
		// nothing happened
		System.out.println("Process complete.");
		return true;
	}
	
	public static boolean inject(File pdf, File output)
	{
		// get the data from pdf and create data JsonObject
		try 
		{
			JsonObject data = new JsonObject();
			PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdf), new PdfWriter(output));
			PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
			form.getFormFields().forEach((String key, PdfFormField value) -> {
				data.add(key, key);
			});
			pdfDoc.close();
			
			// call the inject method with the 
			inject(data, pdf, output);
		} 
		catch (IOException e) 
		{
			System.out.println("An error occurred opening the PDF stream.");
			e.printStackTrace();
			return false;
		}
		
		// nothing happened
		return true;
	}
}
