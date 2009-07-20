package test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class InvocationHandlerTest {
	
	public static void main(String[] args) {
		User target = new UserImpl();
		UserHandler handler = new UserHandler(target);
		User user = (User) Proxy.newProxyInstance(UserHandler.class.getClassLoader(), new Class<?>[] {User.class}, handler);
		try {
			user.getName();
		}
		catch (UserException e) {
			e.printStackTrace();
		}
		
	}

}

class UserHandler implements InvocationHandler {
	
	User target;

	public UserHandler(User user) {
		target = user;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		try {
			return method.invoke(target, args);
		}
		catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
		catch (Exception e) {
			throw e;
		}
	}
}

abstract class User {
	abstract String getName() throws UserException;
}

class UserImpl extends User {

	@Override
	public String getName() throws UserException {
		throw new UserException();
	}
	
}

class UserException extends Exception {
	private static final long serialVersionUID = 1L;
	public UserException() {
		super("This is a user exception.");
	}
}
