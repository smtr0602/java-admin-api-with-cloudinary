package org.kutaka.adminapi.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kutaka.adminapi.config.CloudinaryConfig;
import org.kutaka.adminapi.constants.Cloudinary;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;

public class BookHelper {

  private CloudinaryConfig cloudinaryConfig;

  public BookHelper() {
    this.cloudinaryConfig = CloudinaryConfig.getInstance();
  }

  public Object getImagesFromCloudinary(List<String> folders, String bookType) {
    if (folders.isEmpty())
      return new ArrayList<>();

    ApiResponse cloudinaryRes = null;
    String query = "";
    for (int i = 0; i < folders.size(); i++) {
      if (i == 0) {
        query += "folder:" + Cloudinary.FOLDER_NAMES.ROOT + "/" + bookType + "/" + folders.get(i)
            + "/*/";
        continue;
      }
      query += "OR folder:" + Cloudinary.FOLDER_NAMES.ROOT + "/" + bookType + "/" + folders.get(i)
          + "/*/";
    }
    try {
      cloudinaryRes = cloudinaryConfig.getCloudinary().search()
          .expression(query)
          .maxResults(Cloudinary.SEARCH_PARAMS.MAX_RESULT)
          .execute();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return cloudinaryRes.values().iterator().next();
  }

  public Map uploadCoverImageToCloudinary(MultipartFile image, String bookType, String folderName)
      throws IOException {
    Map response = cloudinaryConfig.getCloudinary().uploader().upload(image.getBytes(),
        ObjectUtils.asMap("resource_type", "auto", "public_id", "cover", "discard_original_filename", true,
            "unique_filename", false, "folder", Cloudinary.FOLDER_NAMES.ROOT + "/" + bookType + "/" + folderName));

    return response;
  }

  public ArrayList<Map> uploadPageImagesToCloudinary(MultipartFile[] images, String bookType, String folderName)
      throws IOException {
    ArrayList<Map> responses = new ArrayList<>();
    for (int i = 0; i < images.length; i++) {
      Map res = cloudinaryConfig.getCloudinary().uploader().upload(images[i].getBytes(),
          ObjectUtils.asMap("resource_type", "auto", "public_id", "page-" + String.format("%03d", i + 1),
              "discard_original_filename", true,
              "unique_filename", false, "folder",
              Cloudinary.FOLDER_NAMES.ROOT + "/" + bookType + "/" + folderName + "/" + Cloudinary.FOLDER_NAMES.PAGES));
      responses.add(res);
    }
    return responses;
  }

  public ApiResponse deleteImagesFromCloudinary(String bookType, String folderName) throws Exception {
    String prefix = Cloudinary.FOLDER_NAMES.ROOT + "/" + bookType + "/" + folderName;
    ;
    ApiResponse response = cloudinaryConfig.getCloudinary().api().deleteResourcesByPrefix(prefix, null);

    return response;
  }
}
