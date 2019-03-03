package com.itstyle.mail.common.util;

import java.io.*;

/**
 * @ClassName SerializeUtil
 * @ProjectName spring-boot-mail
 * @Description TODO
 * @Author 万民
 * @Date 2018/12/21 9:56
 * @Version 1.0
 **/
public class SerializeUtil {
    public static byte[] serualize(Object object){
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return  bytes;
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                if(baos != null)
                    baos.close();
                if(oos != null)
                    oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Object unSerialize(byte[] bytes){
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        Object object = null;
        try {
            bais = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bais);
            object = ois.readObject();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if(bais!=null)
                    bais.close();
                if(ois != null)
                    ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return object;
    }
}
