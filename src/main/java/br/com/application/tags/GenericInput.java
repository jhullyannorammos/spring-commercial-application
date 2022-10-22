package br.com.application.tags;

import org.springframework.web.servlet.tags.form.InputTag;

public class GenericInput extends InputTag{
	
	private String type;
	
	public void setType(String type) {
		this.type = type;
	}

	@Override
	protected String getType() {		
		return type;
	}
}
