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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    final List<Event> preparedEvents = prepareEvents(events, request);
    List<TimeRange> queryResult = new ArrayList<>();
    int currentTime = 0;

    for (Event event : preparedEvents) {
      if (currentTime + request.getDuration() <= event.getWhen().start()) {
        int end = (int) Math.max(event.getWhen().start(), currentTime + request.getDuration());
        queryResult.add(TimeRange.fromStartEnd(currentTime, end, false));
      }
      currentTime = Math.max(currentTime, event.getWhen().end());
    }

    if (currentTime + request.getDuration() <= TimeRange.END_OF_DAY) {
      queryResult.add(TimeRange.fromStartEnd(currentTime, TimeRange.END_OF_DAY, true));
    }

    return queryResult;
  }

  private List<Event> prepareEvents(Collection<Event> events, MeetingRequest request) {
    List<Event> prepared = filterIrrelevantEvents(events, request);
    sortEvents(prepared);
    return prepared;
  }

  private List<Event> filterIrrelevantEvents(Collection<Event> events, MeetingRequest request) {
    return events.stream()
        .filter(event -> {
          Collection<String> eventCopy = new HashSet<>(event.getAttendees());
          eventCopy.retainAll(request.getAttendees());
          return !eventCopy.isEmpty();
        })
        .collect(Collectors.toList());
  }

  private void sortEvents(List<Event> events) {
    events.sort((Event e1, Event e2) -> {
      if (e1.getWhen().start() == e2.getWhen().start()) {
        return Integer.compare(e1.hashCode(), e2.hashCode());
      }
      return Integer.compare(e1.getWhen().start(), e2.getWhen().start());
    });
  }
}
