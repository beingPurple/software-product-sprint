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

public final class FindMeetingQuery {
    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
        Collection<TimeRange> free = new ArrayList<TimeRange>(); //when user is free
        /*
        we are given a collection of event and outputting timeranges as a list
        how do we go from event to time range?

        An event has: 
            private final String title;
            private final TimeRange when;
            private final Set<String> attendees

        a timerange has: 
            public static final int START_OF_DAY = getTimeInMinutes(0, 0);
            public static final int END_OF_DAY = getTimeInMinutes(23, 59);

        So to produce a list of timeranges from a collection of events
        */
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
            Event prev = null;
            Iterator<Event> iterator = events.iterator();
            while ( iterator.hasNext()) {
            Event busy = iterator.next();
            System.out.println("next?: " + iterator.hasNext());
            System.out.println("first?: " + first);
            if(first){      
                //before the event
                freeTime = TimeRange.fromStartEnd(TimeRange.START_OF_DAY, busy.getWhen().start(), false);
                free.add(freeTime);
                first = !first;
                System.out.println("free time start: " + freeTime);
                prev = busy;
                System.out.println("PREVIOUS: " + prev);
            }

            //for in between events
            if(!first && iterator.hasNext()){
                System.out.println("in mid loop");
                System.out.println("PREVIOUS: " + prev);
                freeTime = TimeRange.fromStartEnd(prev.getWhen().end(), busy.getWhen().start() + 60, false);
                //not sure why this is an hour off...
                System.out.println("free time mid: " + freeTime);
                if(busy.getWhen().overlaps(prev.getWhen())
                && prev.getWhen().end() != busy.getWhen().end()
                ){
                    overlap = true;
                }
                if((busy.getWhen().start() + 60) - prev.getWhen().end() < 0
                && !busy.getWhen().overlaps(prev.getWhen())){
                    free.add(freeTime);//it's off by 30m here...
                }
                
            }
            
            if(!iterator.hasNext())
            {//for last event
            System.out.println("in end loop");
            //if they overlap, the end free period is off by 30m
            int s = busy.getWhen().end();
            if(overlap){
                s+= 30;
            }
                freeTime = TimeRange.fromStartEnd(s, TimeRange.END_OF_DAY , true);
                free.add(freeTime);
                System.out.println("free time end: " + freeTime);
            }
            
            }
        }
        System.out.println("free time list: " + free);
        return free;
  }
}
