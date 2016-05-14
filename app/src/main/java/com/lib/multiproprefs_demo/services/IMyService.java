package com.lib.multiproprefs_demo.services;

public interface IMyService extends android.os.IInterface
{
    /** Local-side IPC implementation stub class. */
    public static abstract class Stub extends android.os.Binder implements com.lib.multiproprefs_demo.services.IMyService
    {
        private static final java.lang.String DESCRIPTOR = "com.lib.multiproprefs_demo.services.IMyService";
        /** Construct the stub at attach it to the interface. */
        public Stub()
        {
            this.attachInterface(this, DESCRIPTOR);
        }
        /**
         * Cast an IBinder object into an com.lib.multiproprefs_demo.IMyService interface,
         * generating a proxy if needed.
         */
        public static com.lib.multiproprefs_demo.services.IMyService asInterface(android.os.IBinder obj)
        {
            if ((obj==null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin!=null)&&(iin instanceof com.lib.multiproprefs_demo.services.IMyService))) {
                return ((com.lib.multiproprefs_demo.services.IMyService)iin);
            }
            return new com.lib.multiproprefs_demo.services.IMyService.Stub.Proxy(obj);
        }
        @Override public android.os.IBinder asBinder()
        {
            return this;
        }
        @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
        {
            switch (code)
            {
                case INTERFACE_TRANSACTION:
                {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_getPerson:
                {
                    data.enforceInterface(DESCRIPTOR);
                    java.util.List _result = this.getPerson();
                    reply.writeNoException();
                    reply.writeList(_result);
                    return true;
                }
                case TRANSACTION_addPerson:
                {
                    data.enforceInterface(DESCRIPTOR);
                    com.lib.multiproprefs_demo.aidl.vo.Person _arg0;
                    if ((0!=data.readInt())) {
                        _arg0 = com.lib.multiproprefs_demo.aidl.vo.Person.CREATOR.createFromParcel(data);
                    }
                    else {
                        _arg0 = null;
                    }
                    this.addPerson(_arg0);
                    reply.writeNoException();
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }
        private static class Proxy implements com.lib.multiproprefs_demo.services.IMyService
        {
            private android.os.IBinder mRemote;
            Proxy(android.os.IBinder remote)
            {
                mRemote = remote;
            }
            @Override public android.os.IBinder asBinder()
            {
                return mRemote;
            }
            public java.lang.String getInterfaceDescriptor()
            {
                return DESCRIPTOR;
            }
            @Override public java.util.List getPerson() throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                java.util.List _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_getPerson, _data, _reply, 0);
                    _reply.readException();
                    java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
                    _result = _reply.readArrayList(cl);
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }
            @Override public void addPerson(com.lib.multiproprefs_demo.aidl.vo.Person person) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((person!=null)) {
                        _data.writeInt(1);
                        person.writeToParcel(_data, 0);
                    }
                    else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(Stub.TRANSACTION_addPerson, _data, _reply, 0);
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }
        static final int TRANSACTION_getPerson = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_addPerson = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    }
    public java.util.List getPerson() throws android.os.RemoteException;
    public void addPerson(com.lib.multiproprefs_demo.aidl.vo.Person person) throws android.os.RemoteException;
}
