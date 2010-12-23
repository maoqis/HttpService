package novoda.lib.httpservice.provider.local;

import static novoda.lib.httpservice.util.LogTag.Provider.debug;
import static novoda.lib.httpservice.util.LogTag.Provider.debugIsEnable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.ProviderException;
import java.util.HashMap;

import novoda.lib.httpservice.provider.EventBus;
import novoda.lib.httpservice.provider.Provider;
import novoda.lib.httpservice.request.Request;
import android.net.Uri;

public class LocalProvider implements Provider {
	
	public HashMap<Uri, String> map = new HashMap<Uri, String>();
	
	private EventBus eventBus;
	
	public LocalProvider(EventBus eventBus) {	
		if(eventBus == null) {
			throw new ProviderException("EventBus is null, can't procede");
		}
		this.eventBus = eventBus;
	}
	
	public LocalProvider(EventBus eventBus, Uri uri, String content) {
		this(eventBus);
		map.put(uri, content);
	}
	
	public void add(Uri uri, String content) {
		map.put(uri, content);
	}
	
	public void add(String url, String content) {
		map.put(Uri.parse(url), content);
	}
	
	public InputStream getContent(Uri uri) {
		if(map.containsKey(uri)) {			
			return new ByteArrayInputStream(map.get(uri).getBytes());
		} else {
			if(debugIsEnable()) {
				debug("There is no resource registered for the local provider for url : " + uri);
			}
			return null;
		}
	}
	
	@Override
	public void execute(Request req) {
		InputStream content = getContent(req.getUri());
		if(content == null) {
			eventBus.fireOnThrowable(req, new Throwable("Content not found"));
		}
		eventBus.fireOnContentReceived(req, content);
	}

}
