package lr.server;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.io.InputStream;
import lr.db.IFilesDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Images game shop controller", description = "Controller for operations with images")
@RestController
public class ImageController {
    private static final Logger log = LoggerFactory.getLogger(ImageController.class);

    private final IFilesDAO filesDAO;

    public ImageController(IFilesDAO filesDAO) {
        this.filesDAO = filesDAO;
    }

    @Operation(summary = "Upload game image. Use game id with .jpg postfix for image name")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadFile(@RequestParam("fileToUpload") MultipartFile file) {
        if (file.isEmpty()) {
            return "Please select a file to upload";
        }

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            log.error("Error getting bytes from input file {}.", file.getOriginalFilename(), e);
            return "Error getting bytes from input file.";
        }

        if (filesDAO.upload(file.getOriginalFilename(), bytes)) {
            return "File successfully uploaded";
        }

        return "Error uploading file";
    }

    @Operation(summary = "Get game image.")
    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> getImage(
        @Parameter(description = "Use game id with .jpg postfix for image name") @PathVariable String filename
    ) {
        InputStream imageStream = filesDAO.download(filename);
        if (imageStream == null) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new InputStreamResource(imageStream);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return ResponseEntity.ok()
            .headers(headers)
            .body(resource);
    }
}
