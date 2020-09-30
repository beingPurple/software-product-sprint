// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Iterator;
import java.util.*;

public final class FindMeetingQuery {
    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
        Collection<Event> modEvents = new ArrayList<Event>();
        Collection<TimeRange> free = new ArrayList<TimeRange>(); //when user is free
        TimeRange freeTime = null;
        Event prev = null;
        Event busy = null;
        Boolean passed = false;
        Boolean overlap = false;
        Iterator<Event> iterator = events.iterator();
        int later = 0;
        int sTime = 0;
        int totalFreeTime = 0;
        Collection<String> personInQuestion = request.getAttendees();

        System.out.println("================START=====================");
        //print out whole event list raw
        System.out.println("all events (that are already scheduled) --------");
        //go through each event. does the attendee appear on the request? if not remove from the collection
        ArrayList<String> requestAttendants = new ArrayList<String>();
        ArrayList<Event> notAttendingEvent = new ArrayList<Event>();

        Collection<String> r = request.getAttendees();
        for(String s:r){
            requestAttendants.add(s);
        }
        System.out.println("request attendants: " + requestAttendants);

        for(Event i:events){
            System.out.println("event in question: " + i);
            for(String attend:requestAttendants){
                System.out.println("attendant in question: " + attend);
                if(!i.getAttendees().contains(attend)){
                        System.out.println("removing");
                    }
                else{
                    modEvents.add(i);
                    totalFreeTime += i.getWhen().duration();
                    System.out.println("keeping");
                }
            }
        }
        System.out.println();
        events = modEvents;

        totalFreeTime = TimeRange.WHOLE_DAY.duration() - totalFreeTime;
        System.out.println("size: " + events.size() + 
                            ", total free timne: " + 
                            totalFreeTime);
        System.out.println("meeting attendees: " + request.getAttendees() + ", duration: "+ request.getDuration());

        if(totalFreeTime < request.getDuration()){ //if there is not enough space in the schedule
            System.out.println("not enough time");
            return free;
        }

        if(!passed && request.getDuration() > TimeRange.WHOLE_DAY.duration()){
        //if request is too long
            //return an empty set
            System.out.println("request is too long");
            return free;
        }
        else if(!passed && events.isEmpty()){
        //is user completely free?
            freeTime = TimeRange.WHOLE_DAY;
            System.out.println("free whole day: " + freeTime);
            free.add(freeTime);
            passed = true;
            System.out.println("free: " + free);
        }
        else if (!passed){//if request is a normal ammount
            //take the meeting chunk out of the day and split into two options
            Boolean first = true;
            
            
            
            
            while (!passed && iterator.hasNext()) {
                busy = iterator.next();
                System.out.println("=====================================");
                System.out.println("next event: [" + busy.getWhen().start()+
                " , " + busy.getWhen().end() + "]");
                if(prev != null)
                {System.out.println("prev event: [" + prev.getWhen().start()+
                " , " + prev.getWhen().end() + "]");}
                
                System.out.println("passed?: " + passed);
                System.out.println("next?: " + iterator.hasNext());
                if(first){      
                    //before the event
                    // System.out.println("first");
                    freeTime = TimeRange.fromStartEnd(TimeRange.START_OF_DAY, busy.getWhen().start(), false);
                    if(TimeRange.START_OF_DAY != busy.getWhen().start())
                    {
                        free.add(freeTime);
                        passed = true;
                        System.out.println("adding first free time: " + freeTime);
                    }
                    else {
                        System.out.println("immediately busy");
                        passed = true;
                    }
                    first = !first;
                    prev = busy;
                }

                //for in between events
                if(!passed){
                    
                    if(!prev.getWhen().overlaps(busy.getWhen()))//check for overlaps
                    {
                        if(later != 0){
                            sTime = later;
                            overlap = false;
                            later = 0;
                        }
                        else{
                            sTime= prev.getWhen().end();
                            overlap = false;
                        }
                        freeTime = TimeRange.fromStartEnd(sTime, busy.getWhen().start() , false);
                        System.out.println("adding free time: " + freeTime);
                        free.add(freeTime);
                        passed = true;
                        System.out.println("adding mid event");
                        prev = busy;
                    }
                    else{
                        System.out.println("overlap!");
                        overlap = true;
                        if(prev.getWhen().end() > busy.getWhen().end()){
                            later = prev.getWhen().end();
                            System.out.println("prev ends later: " + later + "[" + busy.getWhen().end() + "]");
                        }
                        else if (busy.getWhen().end() >= prev.getWhen().end()){
                            later = busy.getWhen().end();
                            System.out.println("next event ends later: " + later+ "[" + prev.getWhen().end() + "]");
                        }
                        passed = true;
                    }
                }
                System.out.println("next pass");
                passed = !passed;
                System.out.println("next? " + iterator.hasNext());
            }
        }
        //check to see if event at end
        System.out.println("out of loop. passed yet? " + passed);
        
        System.out.println("are there any left?: " + iterator.hasNext());
        if(!passed){
            System.out.println("in endgame");
            if(busy.getWhen().end() > TimeRange.END_OF_DAY || prev.getWhen().end() > TimeRange.END_OF_DAY)
                {
                    if(later != 0){
                            sTime = later;
                            overlap = false;
                            later = 0;
                    }
                    else{
                        sTime= prev.getWhen().end();
                        overlap = false;
                    }
                    System.out.println("later: " + later + "sTime: " + sTime);
                    freeTime = TimeRange.fromStartEnd(sTime, busy.getWhen().start() , false);
                    
                    System.out.println("event at end of day -> no free time");
                    // free.add(freeTime);
                    passed = true;
                    // System.out.println("adding last free time: " + freeTime);
                }

                else{//if you're free at the end of the day
                    // check if free time is longer than 1 minute
                    System.out.println("free at end");
                    if(prev.getWhen().end() != TimeRange.END_OF_DAY + 1)
                    if(later != 0){
                            sTime = later;
                            overlap = false;
                            later = 0;
                    }
                    else{
                        sTime= prev.getWhen().end();
                        overlap = false;
                    }
                    System.out.println("later: " + later + " sTime: " + sTime);
                    freeTime = TimeRange.fromStartEnd(sTime, TimeRange.END_OF_DAY , true);
                    free.add(freeTime);
                    passed = true;
                    System.out.println("adding last free time: " + freeTime);
                }
            }
        System.out.println("====================DONE PASSING=================");
        System.out.println("free time list: " + free);
        System.out.println();
        return free;
    }
}
