package tech.ggsoft.digitplace.bootstrap;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Bootstrap implements ApplicationListener<ContextRefreshedEvent>{

	@Override
	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
		log.info("ContextRefreshedEvent");
		initData();	
	}
	
	private void initData() {
		
	}

}
