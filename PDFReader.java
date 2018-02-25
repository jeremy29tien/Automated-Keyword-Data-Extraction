import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

/**
 * PDFReader.java
 * This class contains methods for reading 
 * PDFs.
 * @author JeremyTien
 * @since November 6, 2017
 */
public class PDFReader{
	
	private String text;
	private String title;
    
	/**
	 * This method converts a given PDF file to 
	 * plain text, and returns it in the form
	 * of a String.
	 * @param filePath of the input PDF file
	 * @return a String containing the text of the PDF file
	 */
    public String pdfToText(String filePath)
    {
    	PDFTextStripper pdfStripper = null;
        PDDocument pdDoc = null;
        PDDocumentInformation docInfo  = null;
        File file = new File(filePath);
        try {
            pdfStripper = new PDFTextStripper();
            pdDoc = PDDocument.load(file);
            docInfo = pdDoc.getDocumentInformation();
            pdfStripper.setStartPage(1);
            pdfStripper.setEndPage(pdDoc.getPages().getCount());
            text = pdfStripper.getText(pdDoc);
            title = docInfo.getTitle();
            return text;
        } catch (IOException e) {
            System.out.println("Was not able to convert PDF to text.");
            e.printStackTrace();
        }
        return null;
    }
    
    public String getTitle()
    {
    	return title;
    }
}
