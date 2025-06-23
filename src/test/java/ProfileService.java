// package com.group1.project.swp_project.service;

// import org.springframework.stereotype.Service;

// import com.group1.project.swp_project.dto.req.UpdateProfileRequest;
// import com.group1.project.swp_project.entity.Profile;
// import com.group1.project.swp_project.entity.Users;
// import com.group1.project.swp_project.repository.ProfileRepository;
// import com.group1.project.swp_project.repository.UserRepository;

// @Service
// public class ProfileService {

// private final UserRepository userRepository;
// private final ProfileRepository profileRepository;

// public ProfileService(UserRepository userRepository, ProfileRepository
// profileRepository) {
// this.userRepository = userRepository;
// this.profileRepository = profileRepository;
// }

// /**
// * Lấy thông tin hồ sơ của người dùng
// */
// // public Profile getProfileByUser(Users user) {
// // return profileRepository.findByUser(user)
// // .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ người
// dùng"));
// // }

// // /**
// // * Cập nhật thông tin người dùng và hồ sơ
// // */
// // public Users updateProfile(Users user, UpdateProfileRequest dto) {
// // Profile profile = profileRepository.findByUser(user)
// // .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ người
// dùng"));

// // // Cập nhật Profile
// // if (dto.getFullName() != null)
// // profile.setFullName(dto.getFullName());

// // if (dto.getGender() != null)
// // profile.setGender(dto.getGender());

// // if (dto.getDateOfBirthday() != null)
// // profile.setdateOfBirth(dto.getDateOfBirthday());

// // if (dto.getAddress() != null)
// // profile.setAddress(dto.getAddress());

// // if (dto.getAvatarUrl() != null)
// // profile.setAvatarUrl(dto.getAvatarUrl());

// // // Cập nhật User (email, phone)
// // if (dto.getEmail() != null)
// // user.setEmail(dto.getEmail());

// // if (dto.getPhone() != null)
// // user.setPhone(dto.getPhone());

// // // Gán lại profile cho user nếu cần
// // user.setProfile(profile);

// // return userRepository.save(user); // do Cascade.ALL nên profile cũng sẽ
// được lưu
// // }

// }
