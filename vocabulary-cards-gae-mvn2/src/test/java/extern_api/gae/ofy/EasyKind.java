package extern_api.gae.ofy;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class EasyKind {
	@Id
	public Long id;
	
	//List<Key<EasyKind>> l = new ArrayList
	public Key<EasyKind> k;
	
	public EasyKind() {}
}
