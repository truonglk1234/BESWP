package com.group1.project.swp_project.service;

import com.group1.project.swp_project.entity.Topic;
import com.group1.project.swp_project.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicService {

    @Autowired
    private TopicRepository topicRepository;

    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    public Topic createTopic(String name) {
        String cleanedName = name.replace("\"", "").trim();
        if (topicRepository.findByName(cleanedName).isPresent()) {
            throw new RuntimeException("Chủ đề đã tồn tại.");
        }
        return topicRepository.save(Topic.builder().name(name).build());
    }
}
