package de.hftstuttgart.rest.v1.unittest;

import de.hftstuttgart.utils.FileUtil;
import de.hftstuttgart.utils.UnzipUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.annotation.MultipartConfig;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Rest controller for anything related to the TEST files.
 */
@RestController
@RequestMapping("/v1/unittest")
@MultipartConfig()
public class UnitTestUpload {
    private static final Logger LOG = Logger.getLogger(UnitTestUpload.class);

    @Value("${mojec.dir.parent}")
    private String parentDir;

    @Value("${mojec.dir.assignment.prefix}")
    private String folderNamePrefix;

    @Value("${mojec.dir.test.folder.name}")
    private String testFolderName;

    /**
     * Create a subfolder for the specific assignment.
     * This is called when the teacher creates an assignment and uploads the JUnit test files
     *
     * @param unitTestFileRef   The zip file which contains the JUnit tests
     * @param assignmentId      ID of the created assignment. Generated by Moodle
     */
    @RequestMapping(method = RequestMethod.POST)
    public void uploadUnitTestFile(@RequestParam("unitTestFile") MultipartFile unitTestFileRef, @RequestParam("assignmentId") String assignmentId) throws IOException {
        // Create one folder per assignment
        String subFolderPath = parentDir + File.separator + folderNamePrefix + assignmentId + File.separator + testFolderName;
        new File(subFolderPath).mkdirs();
        File file = new File(subFolderPath, String.valueOf(UUID.randomUUID()));
        unitTestFileRef.transferTo(file);
        UnzipUtil.unzip(file);

        LOG.info("Uploaded unit test file: " + file);
    }

    /**
     * Delete the folder for the assignment.
     * Called when the teacher deletes the JUnitTest assignment
     *
     * {{url}}:8080/v1/unittest?assignmentId=111
     *
     * @param assignmentId      ID of the assignment to delete. Generated by Moodle
     */
    @RequestMapping(method = RequestMethod.DELETE)
    public void deleteUnitTestFiles(@RequestParam("assignmentId") String assignmentId) {
        String path = parentDir + File.separator + folderNamePrefix + assignmentId;
        File dir = new File(path);
        FileUtil.deleteFolderRecursively(dir);
    }
}