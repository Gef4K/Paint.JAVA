package heroesgrave.paint.main;

import heroesgrave.utils.io.ImageLoader;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.swing.filechooser.FileFilter;

public abstract class ImageExporter extends FileFilter {
	public static ArrayList<ImageExporter> exporters = new ArrayList<ImageExporter>();
	
	static
	{
		exporters.add(new ImageExporter()
		{
			@Override public String getFileExtension() {
				return "png";
			}
			
			@Override public void exportImage(BufferedImage image, File destination) {
				ImageLoader.writeImage(image, getFileExtension().toUpperCase(), destination.getAbsolutePath());
			}

			@Override
			public String getFileExtensionDescription() {
				return "PNG - Portable Network Graphics Image";
			}
		});
		
		exporters.add(new ImageExporter()
		{
			@Override public String getFileExtension() {
				return "bmp";
			}
			
			@Override public void exportImage(BufferedImage image, File destination) {
				ImageLoader.writeImage(image, getFileExtension().toUpperCase(), destination.getAbsolutePath());
			}

			@Override
			public String getFileExtensionDescription() {
				return "BMP - Microsoft Bitmap Image";
			}
		});
		
		exporters.add(new ImageExporter()
		{
			@Override public String getFileExtension() {
				return "gif";
			}
			
			@Override public void exportImage(BufferedImage image, File destination) {
				ImageLoader.writeImage(image, getFileExtension().toUpperCase(), destination.getAbsolutePath());
			}

			@Override
			public String getFileExtensionDescription() {
				return "GIF - Graphics Interchange Format";
			}
		});
		
		exporters.add(new ImageExporter()
		{
			@Override public String getFileExtension() {
				return "jpeg";
			}
			
			@Override public void exportImage(BufferedImage imageIn, File destination) {
				// 'Color Corruption' Fix
				BufferedImage imageOut = new BufferedImage(imageIn.getWidth(), imageIn.getHeight(), BufferedImage.TYPE_INT_RGB); 
				Graphics g = imageOut.getGraphics();
				g.drawImage(imageIn, 0, 0, null);
				
				ImageLoader.writeImage(imageOut, getFileExtension().toUpperCase(), destination.getAbsolutePath());
			}

			@Override
			public String getFileExtensionDescription() {
				return "JPEG - JPEG File Interchange Format";
			}
		});
		
	}
	
	/**
	 * Returns the file extension this ImageExporter exports to.<br>
	 * (Lowercase, without the dot!)
	 **/
	public abstract String getFileExtension();
	
	public abstract String getFileExtensionDescription();
	
	public abstract void exportImage(BufferedImage image, File destination);
	
	@Override
	public boolean accept(File f) {
		if(f.isDirectory())
			return true;
		
		if(f.getAbsolutePath().endsWith("." + getFileExtension()))
			return true;
		
		return false;
	}
	
	@Override
	public String getDescription() {
		return getFileExtensionDescription();
	}

	public static ImageExporter get(String extension) {
		
		for(ImageExporter exporter : exporters){
			if(exporter.getFileExtension().equalsIgnoreCase(extension)){
				return exporter;
			}
		}
		
		throw new RuntimeException("Image Exporter for the given Format '"+extension+"' could not be found!");
	}

	public static void add(ImageExporter exporter) {
		if(exporter == null)
			throw new IllegalArgumentException("Input cannot be null!");
		
		exporters.add(exporter);
	}

}
