package web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileController {
	
	private static final String UPLOADED_FILES_FOLDER = "uploaded-files";
	
	private String uploadedPath;
	
	@Autowired
	public FileController(ServletContext servletContext) {
		uploadedPath = servletContext.getRealPath("/") + UPLOADED_FILES_FOLDER + "/";
		File folder = new File(uploadedPath);
		
		if (!folder.exists()) {
			folder.mkdir();
		}
	}
	
	@RequestMapping(value = "/exists")
	public @ResponseBody String isFileExists(@RequestParam("fileName") String fileName) {
		File file = new File(uploadedPath + fileName);
		
		return file.exists() ? "yes" : "no";
	}
	
	@RequestMapping(value = "/upload", method = POST)
	public @ResponseBody String upload(@RequestParam("file") MultipartFile uploadedFile) throws IOException {
		byte[] bytes = uploadedFile.getBytes();
		File file = new File(uploadedPath + uploadedFile.getOriginalFilename());
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
        stream.write(bytes);
        stream.close();
        return getFileNames();
	}
	
	@RequestMapping(value = "/files", method = GET)
	public @ResponseBody String getFileNames() {
		File folder = new File(uploadedPath);
		
		String[] files = folder.list();
		StringBuilder csv = new StringBuilder();

		int i = 0;
		
		if (files.length == 1) {
			return files[0];
		}

		while (i < files.length - 1) {
			csv
				.append(files[i++])
				.append("|");
		}

		csv.append(files[i]);

		return csv.toString();
	}
	
	@RequestMapping(value = "/delete", method = GET)
	public @ResponseBody String deleteFile(@RequestParam("fileName") String fileName) {
		File file = new File(uploadedPath + fileName);
		file.delete();
		return getFileNames();
	}
}
