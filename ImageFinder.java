/**
 ImageFinder.java
 Last update: 11-06-2017
 */

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observer;

import org.apache.pdfbox.*;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.tools.PDFToImage;

public class ImageFinder {

	public List<RenderedImage> getImagesFromPDF(PDDocument document) throws IOException {
        List<RenderedImage> images = new ArrayList<>();
	    for (PDPage page : document.getPages()) {
	        images.addAll(getImagesFromResources(page.getResources()));
	    }
	
	    return images;
	}

	private List<RenderedImage> getImagesFromResources(PDResources resources) throws IOException {
	    List<RenderedImage> images = new ArrayList<>();
	    for (COSName xObjectName : resources.getXObjectNames()) {
	        PDXObject xObject = resources.getXObject(xObjectName);
	
	        if (xObject instanceof PDFormXObject) {
	            images.addAll(getImagesFromResources(((PDFormXObject) xObject).getResources()));
	        } else if (xObject instanceof PDImageXObject) {
	            images.add(((PDImageXObject) xObject).getImage());
	        }
	    }
	
	    return images;
		}
	}
