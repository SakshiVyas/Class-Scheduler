package com.campusscheduler.algorithm;

import com.campusscheduler.model.*;
import com.campusscheduler.service.ClassOccurrenceGenerator;
import com.campusscheduler.service.TimeOverlapChecker;
import com.campusscheduler.service.TimeSlotGenerator;
import com.campusscheduler.service.ScheduleTimeValidator;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class GreedySolver {


    private final TimeOverlapChecker timeOverlapChecker =
            new TimeOverlapChecker();

    private final ScheduleTimeValidator scheduleTimeValidator =
            new ScheduleTimeValidator();

// first sort the courses in desc
public List<ClassOccurrence> sortOccurrencesBySize(
        List<ClassOccurrence> occurrences
) {
    List<ClassOccurrence> sortedOccurrences =
            new ArrayList<>(occurrences);

    sortedOccurrences.sort(
            Comparator.comparingInt(
                    (ClassOccurrence occurrence) ->
                            occurrence.getCourse()
                                    .getEnrolledStudents()
            ).reversed()
    );

    return sortedOccurrences;
}

    // whether room has enough capacity for this course
    private boolean hasEnoughCapacity(
            ClassOccurrence occurrence,
            Room room
    ) {
        return room.getCapacity()
                >= occurrence.getCourse()
                .getEnrolledStudents();
    }
    // check whether room is allocated already
    private boolean isRoomBusy(
            Room room,
            TimeSlot timeSlot,
            ClassOccurrence occurrence,
            List<ScheduleEntry> schedule
    ) {

        for (ScheduleEntry entry : schedule) {

            boolean sameRoom =
                    entry.getRoom()
                            .getId()
                            .equals(room.getId());

            boolean overlaps =
                    timeOverlapChecker.overlaps(
                            occurrence,
                            timeSlot,
                            entry.getClassOccurrence(),
                            entry.getTimeSlot()
                    );

            if (sameRoom && overlaps) {
                return true;
            }
        }

        return false;
    }
    // checking if professor is busy
    private boolean isProfessorBusy(
            ClassOccurrence occurrence,
            TimeSlot timeSlot,
            List<ScheduleEntry> schedule
    ) {

        String professorId =
                occurrence.getCourse()
                        .getProfessorId();

        for (ScheduleEntry entry : schedule) {

            String scheduledProfessorId =
                    entry.getClassOccurrence()
                            .getCourse()
                            .getProfessorId();

            boolean sameProfessor =
                    professorId.equals(
                            scheduledProfessorId
                    );

            boolean overlaps =
                    timeOverlapChecker.overlaps(
                            occurrence,
                            timeSlot,
                            entry.getClassOccurrence(),
                            entry.getTimeSlot()
                    );

            if (sameProfessor && overlaps) {
                return true;
            }
        }

        return false;
    }
        // checking students belong to same groups --- have same courses
        private boolean shareStudentGroup(
                ClassOccurrence first,
                ClassOccurrence second
        ) {
            List<String> firstGroups =
                    first.getCourse().getStudentGroups();

            List<String> secondGroups =
                    second.getCourse().getStudentGroups();

            if (firstGroups == null || secondGroups == null) {
                return false;
            }

            for (String group : firstGroups) {

                if (secondGroups.contains(group)) {
                    return true;
                }
            }

            return false;
        }
        // conflict happens at the same time
        private boolean hasStudentGroupConflict(
                ClassOccurrence classOccurrence,
                TimeSlot timeSlot,
                List<ScheduleEntry> currentSchedule
        ) {

            for (ScheduleEntry entry : currentSchedule) {

                boolean sharesGroup =
                        shareStudentGroup(
                                classOccurrence,
                                entry.getClassOccurrence()
                        );

                boolean overlaps =
                        timeOverlapChecker.overlaps(
                                classOccurrence,
                                timeSlot,
                                entry.getClassOccurrence(),
                                entry.getTimeSlot()
                        );

                if (sharesGroup && overlaps) {
                    return true;
                }
            }

            return false;
        }

    // checking if all conditions are valid
    private boolean isValidAssignment(
            ClassOccurrence classOccurrence,
            Room room,
            TimeSlot timeSlot,
            List<ScheduleEntry> currentSchedule,
            ScheduleSettings settings
    ){
        if(!hasEnoughCapacity(classOccurrence,room)){
            return false;
        }
        if(isRoomBusy(
                room,
                timeSlot,
                classOccurrence,
                currentSchedule
        )){
            return false;
        }
        if(isProfessorBusy(classOccurrence, timeSlot, currentSchedule)){
            return false;
        }
        if(hasStudentGroupConflict(classOccurrence, timeSlot, currentSchedule)){
            return false;
        }
        if (!scheduleTimeValidator.fitsWithinWorkingHours(
                classOccurrence,
                timeSlot,
                settings
        )) {
            return false;
        }
        return true;
    }


    public ScheduleResult solve(ConstraintData data){

        TimeSlotGenerator timeSlotGenerator =
                new TimeSlotGenerator();

        List<TimeSlot> timeSlots =
                timeSlotGenerator.generate(
                        data.getScheduleSettings()
                );
        ScheduleResult result = new ScheduleResult();
        ClassOccurrenceGenerator generator = new ClassOccurrenceGenerator();
        List<ClassOccurrence> classOccurrences = generator.generate(data.getClasses());

        List<ClassOccurrence> sortedOccurrences =
                sortOccurrencesBySize(classOccurrences);
        for(ClassOccurrence classOccurrence : sortedOccurrences){
            boolean assigned = false;
           // System.out.println("Trying to schedule:" + course.getId());
            for(TimeSlot timeSlot : timeSlots){
                //System.out.println("Trying timeSlot:" +timeSlot.getId());
                for(Room room : data.getRooms()){
                    boolean valid = isValidAssignment(classOccurrence,room,timeSlot,result.getScheduledEntries(),data.getScheduleSettings());
                    //System.out.println("Room:" + room.getId() + "| Valid:" + valid);
                    if (valid) {
                        int wastedSeats =
                                room.getCapacity() - classOccurrence.getCourse().getEnrolledStudents();
                        ScheduleEntry entry = new ScheduleEntry(
                                        classOccurrence, room, timeSlot, wastedSeats);
                        result.addScheduledEntry(entry);
                        assigned = true;
                        break;
                       // System.out.println("    SCHEDULED in " + room.getId() + " at " + timeSlot.getId());
                    }
                }
                if(assigned){
                    break;
                }
            }
            if (!assigned) {
                result.addUnscheduledOccurrence(classOccurrence);
            }
        }
        return result;
    }
}
