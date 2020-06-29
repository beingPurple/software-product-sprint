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
        Collection<TimeRange> free = new ArrayList<TimeRange>(); //when user is free
        TimeRange freeTime = null;

        if(request.getDuration() > TimeRange.WHOLE_DAY.duration()){
        //if request is too long
                    //return an empty set
                }
        else if(events.isEmpty()){
        //is user completely free?
                    freeTime = TimeRange.WHOLE_DAY;
                    free.add(freeTime);
                }
        

        else {//if request is a normal ammount
            //take the meeting chunk out of the day and split into two options
            Boolean first = true;
            Boolean overlap = false;
            Boolean passed = false;
            Event prev = null;
            Iterator<Event> iterator = events.iterator();
            while (!passed && iterator.hasNext()) {
                Event busy = iterator.next();
                System.out.println("=====================================");
                
                System.out.println("next event: [" + busy.getWhen().start()+
                " , " + busy.getWhen().end() + "]");
                if(prev != null)
                {System.out.println("prev event: [" + prev.getWhen().start()+
                " , " + prev.getWhen().end() + "]");}
                if(first){      
                    //before the event
                    // System.out.println("first");
                    freeTime = TimeRange.fromStartEnd(TimeRange.START_OF_DAY, busy.getWhen().start(), false);
                    if(TimeRange.START_OF_DAY != busy.getWhen().start())
                    {
                        free.add(freeTime);
                        passed = true;
                        System.out.println("adding first event");
                    }
                    else {
                        System.out.println("immediately busy");

                    }
                    first = !first;
                    prev = busy;
                }

                //for in between events
                if(!passed && iterator.hasNext()){
                    freeTime = TimeRange.fromStartEnd(prev.getWhen().end(), busy.getWhen().start() , false);
                    System.out.println("adding free time: " + freeTime);
                    //not sure why this is an hour off...
                    // if(busy.getWhen().overlaps(prev.getWhen())
                    // && prev.getWhen().end() != busy.getWhen().end()
                    // ){
                    //     overlap = true;
                    // }
                    // if((busy.getWhen().start() + 60) - prev.getWhen().end() < 0
                    // && !busy.getWhen().overlaps(prev.getWhen())){
                        free.add(freeTime);//it's off by 30m here...
                        passed = true;
                        System.out.println("adding mid event");
                // }
                
            }
            
            if(!passed && !iterator.hasNext())
            {//for last event
                // System.out.println("end");
                //if they overlap, the end free period is off by 30m
                int s = busy.getWhen().end();
                // if(overlap){
                //     s+= 30;
                // }
                freeTime = TimeRange.fromStartEnd(s, TimeRange.END_OF_DAY , true);
                if(busy.getWhen().end() != TimeRange.END_OF_DAY+1){
                    free.add(freeTime);
                    passed = true;
                    System.out.println("adding last event");
                }
            }
            passed = false;
            }
        }
        System.out.println("free time list: " + free);
        return free;
  }
}
