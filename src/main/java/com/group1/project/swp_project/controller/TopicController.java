package com.group1.project.swp_project.controller;

import com.group1.project.swp_project.entity.Topic;
import com.group1.project.swp_project.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

    @Autowired
    private TopicService topicService;

    @GetMapping
    public List<Topic> getAllTopics() {
        return topicService.getAllTopics();
    }

    @PostMapping
    public Topic createTopic(@RequestBody String topicName) {
        return topicService.createTopic(topicName);
    }
}
