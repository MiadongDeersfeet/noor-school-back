package com.noorschool.word.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/words")
public class WordController {
	
	@GetMapping
	public void selectRandomWords() {
		
	}

}
