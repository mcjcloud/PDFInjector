package PDFInjector.pdfinjector;

import java.io.File;
import java.io.IOException;

import com.eclipsesource.json.JsonObject;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

public class Injector 
{	
	public static boolean inject(JsonObject data, File pdf, File output)
	{
		// inject data into output pdf
		PdfDocument pdfDoc;
		try
		{
			pdfDoc = new PdfDocument(new PdfReader(pdf), new PdfWriter(output));
		}
		catch(IOException ioe)
		{
			System.out.println("An error occurred opening the PDF stream.");
			return false;
		}
		
		
		
		// nothing happened
		return true;
	}
	
	
}
