package org.openspotlight.storage.domain;

import java.lang.ref.WeakReference;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 22, 2010
 * Time: 7:39:24 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class WeakReferenceResult<T> {

    public abstract T load();
    
    private WeakReference<T> weakReference=null;

    public synchronized  T getReference(){
        T result = weakReference!=null?weakReference.get():null;
        if(result==null){
            result = load();
            weakReference = new WeakReference<T>(result);
        }
        return result;
    }

}
