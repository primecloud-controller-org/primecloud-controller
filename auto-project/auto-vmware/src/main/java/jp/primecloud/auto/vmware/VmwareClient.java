/*
 * Copyright 2014 by SCSK Corporation.
 * 
 * This file is part of PrimeCloud Controller(TM).
 * 
 * PrimeCloud Controller(TM) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * 
 * PrimeCloud Controller(TM) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PrimeCloud Controller(TM). If not, see <http://www.gnu.org/licenses/>.
 */
package jp.primecloud.auto.vmware;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;

/**
 * <p>
 * TODO: クラスコメントを記述
 * </p>
 *
 */
public class VmwareClient {

    private String url;

    private String username;

    private String password;

    private boolean ignoreCert = false;

    private String datacenterName;

    protected ServiceInstance serviceInstance;

    protected Datacenter datacenter;

    /**
     * TODO: コンストラクタコメントを記述
     *
     * @param url
     * @param username
     * @param password
     * @param ignoreCert
     */
    public VmwareClient(String url, String username, String password, boolean ignoreCert) {
        this(url, username, password, ignoreCert, null);
    }

    /**
     * TODO: コンストラクタコメントを記述
     *
     * @param url
     * @param username
     * @param password
     * @param ignoreCert
     * @param datacenterName
     */
    public VmwareClient(String url, String username, String password, boolean ignoreCert, String datacenterName) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.ignoreCert = ignoreCert;
        this.datacenterName = datacenterName;
    }

    protected void initialize() {
        // ServiceInstanceの作成
        URL url;
        try {
            url = new URL(this.url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        try {
            serviceInstance = new ServiceInstance(url, username, password, ignoreCert);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        // Datacenterの取得
        if (datacenterName != null) {
            InventoryNavigator navigator = new InventoryNavigator(serviceInstance.getRootFolder());
            try {
                datacenter = (Datacenter) navigator.searchManagedEntity("Datacenter", datacenterName);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            if (datacenter == null) {
                // TODO: 例外処理
                throw new RuntimeException("データセンターが見つからない");
            }
        }
    }

    /**
     * TODO: メソッドコメントを記述
     */
    public void logout() {
        if (serviceInstance != null) {
            serviceInstance.getServerConnection().logout();
        }
    }

    /**
     * serviceInstanceを取得します。
     *
     * @return serviceInstance
     */
    public ServiceInstance getServiceInstance() {
        if (serviceInstance == null) {
            initialize();
        }
        return serviceInstance;
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @return
     */
    public ManagedEntity getRootEntity() {
        if (serviceInstance == null) {
            initialize();
        }
        if (datacenter != null) {
            return datacenter;
        } else {
            return serviceInstance.getRootFolder();
        }
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param <T>
     * @param type
     * @param name
     * @return
     */
    public <T extends ManagedEntity> T search(Class<T> type, String name) {
        return search(getRootEntity(), type, name);
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param <T>
     * @param rootEntity
     * @param type
     * @param name
     * @return
     */
    public <T extends ManagedEntity> T search(ManagedEntity rootEntity, Class<T> type, String name) {
        InventoryNavigator navigator = new InventoryNavigator(rootEntity);
        ManagedEntity entity;
        try {
            entity = navigator.searchManagedEntity(type.getSimpleName(), name);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        return type.cast(entity);
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param name
     * @return
     */
    public ManagedEntity searchByName(String name) {
        return searchByName(getRootEntity(), name);
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param rootEntity
     * @param name
     * @return
     */
    public ManagedEntity searchByName(ManagedEntity rootEntity, String name) {
        InventoryNavigator navigator = new InventoryNavigator(rootEntity);
        ManagedEntity entity;
        try {
            entity = navigator.searchManagedEntity("ManagedEntity", name);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param <T>
     * @param type
     * @return
     */
    public <T extends ManagedEntity> ManagedEntity[] searchByType(Class<T> type) {
        return searchByType(getRootEntity(), type);
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param <T>
     * @param rootEntity
     * @param type
     * @return
     */
    public <T extends ManagedEntity> ManagedEntity[] searchByType(ManagedEntity rootEntity, Class<T> type) {
        InventoryNavigator navigator = new InventoryNavigator(rootEntity);
        ManagedEntity[] entities;
        try {
            entities = navigator.searchManagedEntities(type.getSimpleName());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        return entities;
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @return
     */
    public ManagedEntity[] searchAll() {
        return searchAll(getRootEntity());
    }

    /**
     * TODO: メソッドコメントを記述
     *
     * @param rootEntity
     * @return
     */
    public ManagedEntity[] searchAll(ManagedEntity rootEntity) {
        InventoryNavigator navigator = new InventoryNavigator(rootEntity);
        ManagedEntity[] entities;
        try {
            entities = navigator.searchManagedEntities(true);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        return entities;
    }

}
