package org.kutaka.adminapi.config;

import java.util.Map;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

public class CloudinaryConfig {

  private static CloudinaryConfig cloudinaryConfig;
  private Map config = ObjectUtils.asMap(
      "cloud_name", System.getenv("CLOUDINARY_NAME"),
      "api_key", System.getenv("CLOUDINARY_API_KEY"),
      "api_secret", System.getenv("CLOUDINARY_API_SECRET"),
      "secure", true);
  private Cloudinary cloudinary;

  private CloudinaryConfig() {
    this.cloudinary = new Cloudinary(config);
  }

  public static CloudinaryConfig getInstance() {
    // create singleton object if not exists
    if (cloudinaryConfig == null) {
      cloudinaryConfig = new CloudinaryConfig();
    }
    return cloudinaryConfig;
  }

  public Cloudinary getCloudinary() {
    return cloudinary;
  }
}
