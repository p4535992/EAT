package util;

import p4535992.util.http.HttpUtil;

import java.net.URISyntaxException;

public class Test_Http {
	public static void main (String[] args) throws URISyntaxException {
            String a = HttpUtil.getAuthorityName("http://www.unifi.it");
            System.out.println(a);
	}
}
