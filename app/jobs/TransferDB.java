package jobs;

import models.Data;
import models.DataTransfer;
import play.db.jpa.JPA;
import play.jobs.Every;
import play.jobs.Job;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by AnhQuan on 8/11/2016.
 */
@Every("180s")
public class TransferDB extends Job {
    @Override
    public void doJob(){
      //  Data.find()
        EntityManager em = JPA.em();
        Query query= em.createNativeQuery("SELECT distinct sensor_id FROM Data");
        List<Number> listSensor=(List<Number>)query.getResultList();
        //Long a= listSensor.get(1);
        query=em.createNativeQuery("SELECT Distinct node_id FROM Data");
        List<Number> listNode=(List<Number>)query.getResultList();

        for(Number i:listSensor){
            Long idNode=i.longValue();
            for(Number j:listSensor){
                Long idSensor=j.longValue();
                List<Data> listData=Data.find("byNode_idAndSensor_id",idNode,idSensor).fetch();
                DataTransfer dataTransfer=new DataTransfer(calculateMedium(listData),idNode,idSensor);
                dataTransfer.save();
            }
        }
    }

    private static float calculateMedium(List<Data> list){
        float value=0;
        for(Data data:list){
            value+=data.value;
            data.delete();
        }
        if(list.size()>0){
            return value/list.size();
        }else{
            return 0;
        }
    }
}