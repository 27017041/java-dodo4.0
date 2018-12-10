package com.embraiz.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class AppInfo {

	private List<String> accessUrls;

	public List<String> getAccessUrls() {
		accessUrls = new ArrayList<String>();
		accessUrls.add("signIn");
		accessUrls.add("error");
		accessUrls.add("getGlobalLabel");
		accessUrls.add("getpdf");
		accessUrls.add("testInvoice");
		accessUrls.add("printPDF");
		accessUrls.add("testmail");
		accessUrls.add("exportFile");

		return accessUrls;
	}

}
