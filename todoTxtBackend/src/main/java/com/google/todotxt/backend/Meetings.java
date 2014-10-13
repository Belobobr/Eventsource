package com.google.todotxt.backend;

/**
 * Created by Newshka on 25.09.2014.
 */

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiReference;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.users.User;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import static com.google.todotxt.backend.OfyService.ofy;

@ApiReference(Greetings.class)
@Api(name = "meetings")
public class Meetings {

    public static ArrayList<HelloGreeting> meetings = new ArrayList<HelloGreeting>();

    static {
        meetings.add(new HelloGreeting("Samara"));
        meetings.add(new HelloGreeting("Kinel"));
    }

    public ArrayList<HelloGreeting> listGreeting() {
        return meetings;
    }

    /**
     *  Метод возвращающий список встреч соответствующих определенному фильтру
     * @param meetFilter фильтр, который выбрал пользователь и переслал его нам
     * @param count число встреч которое нам должны вернуть
     * @param user пользователь
     * @return
     */
    @ApiMethod(name = "meetings.list", httpMethod = "post")
    public CollectionResponse<MeetRecord> listMeetings(@Named("count") int count, MeetFilter meetFilter, User user) {
        ArrayList<Meet> meetList = new ArrayList<Meet>();
        List<MeetRecord> records = ofy().load().type(MeetRecord.class).limit(count).list();
        return CollectionResponse.<MeetRecord>builder().setItems(records).build();
    }

        /**
         * Метод добавляющий пользователя в опреденную встречу
         * @param meet встреча в которую добавляется пользователь
         * @param user пользователь добавляющийся во встречу
         */
    @ApiMethod(name = "meetings.addUserToMeet", httpMethod = "post")
    public void addUserToMeet(Meet meet, User user) {

    }

    /**
     * Метод добавляющий встречу, делает владельцем пользователя добавившего её
     * @param meet встреча которую хочет добвить пользователь
     * @param user пользователь который создает встречу
     */
    @ApiMethod(name = "meetings.addMeet", httpMethod = "post")
    public void addMeet(Meet meet, User user) {
        MeetRecord meetRecord = new MeetRecord();
        meetRecord.setDescription(meet.getDescription());
        meetRecord.setName(meet.getName());
        meetRecord.setPlace(meet.getPlace());
        ofy().save().entity(meetRecord).now();
    }

    @ApiMethod(name ="meetings.editMeet", httpMethod = "post")
    public void editMeet(MeetRecord meetRecord, User user) {
       MeetRecord searchedMeetRecord = findMeet(meetRecord);
       if (searchedMeetRecord != null) {
           ofy().delete().entity(searchedMeetRecord).now();
           ofy().save().entity(meetRecord).now();
       } else {
           //Ошибка не смогли найти такую запись.. тогда просто создадим новую =)
           ofy().save().entity(meetRecord).now();
       }
    }

    private MeetRecord findMeet(MeetRecord meetRecord) {
        return ofy().load().type(MeetRecord.class).id(meetRecord.getId()).now();
    }

}
