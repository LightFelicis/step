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
import java.util.Optional;
import java.util.stream.Collectors;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    List<TimeRange> requiredTimeranges = findMeetingTimeranges(events, request.getDuration(),
        request.getAttendees());
    List<TimeRange> optionalTimeranges = findMeetingTimeranges(events, request.getDuration(),
        request.getOptionalAttendees());
    List<TimeRange> intersection = findTimerangesIntersections(requiredTimeranges,
        optionalTimeranges, request.getDuration());
    if (intersection.isEmpty()) {
      return requiredTimeranges;
    }
    return intersection;
  }

  final List<TimeRange> findTimerangesIntersections(List<TimeRange> required,
                                                    List<TimeRange> optional,
                                                    long duration) {
    List<TimeRange> intersection = new ArrayList<>();
    for (TimeRange requiredTimeRange : required) {
      for (TimeRange optionalTimeRange : optional) {
        Optional<TimeRange> tr = createIntersection(requiredTimeRange, optionalTimeRange);
        if (tr.isPresent()) {
          intersection.add(tr.get());
        }
      }
    }
    return intersection.stream()
        .filter(timeRange -> timeRange.duration() >= duration)
        .collect(Collectors.toList());
  }

  private Optional<TimeRange> createIntersection(TimeRange a, TimeRange b) {
    Optional<TimeRange> result = Optional.empty();
    if (a.overlaps(b)) {
      TimeRange intersection = TimeRange.fromStartEnd(Math.max(a.start(), b.start()),
          Math.min(a.end(), b.end()), false);
      result = Optional.of(intersection);
    }
    return result;
  }

  private List<TimeRange> findMeetingTimeranges(Collection<Event> events, long duration,
                                                Collection<String> attendees) {
    List<Event> preparedEvents = prepareEvents(events, attendees);
    List<TimeRange> queryResult = new ArrayList<>();
    int currentTime = 0;
    for (Event event : preparedEvents) {
      if (currentTime + duration <= event.getWhen().start()) {
        int end = (int) Math.max(event.getWhen().start(), currentTime + duration);
        queryResult.add(TimeRange.fromStartEnd(currentTime, end, false));
      }
      currentTime = Math.max(currentTime, event.getWhen().end());
    }

    if (currentTime + duration <= TimeRange.END_OF_DAY) {
      queryResult.add(TimeRange.fromStartEnd(currentTime, TimeRange.END_OF_DAY, true));
    }

    return queryResult;
  }

  private List<Event> prepareEvents(Collection<Event> events, Collection<String> attendees) {
    List<Event> prepared = filterIrrelevantEvents(events, attendees);
    sortEvents(prepared);
    return prepared;
  }

  private List<Event> filterIrrelevantEvents(Collection<Event> events, Collection<String> requestAttendees) {
    return events.stream()
        .filter(event -> {
          Collection<String> eventCopy = new HashSet<>(event.getAttendees());
          eventCopy.retainAll(requestAttendees);
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
