# Calendar Service

### Overview
Application is designed to be a backend for calendar-like API.

### Third party libraries
- Java 8
- Spring boot 2
- H2 database
- Spock framework & Groovy & dbUnit for tests
- Maven

### Usage

#### App configuration
Application has couple of settings

|property|description|
|----|----|
|calendar.service.security.enabled| When property is set to true it is only available to create new user w/o entering credentials|
|calendar.minimal.meeting.slot.minutes | Sets the minimal meeting time |
|calendar.recurrence.duration.days| For DAILY meetings controls the last meeting date| 

#### Start application
-From IDE:
Main class is [CalendarServiceApplication](src/main/java/calendar/app/CalendarServiceApplication.java)

H2 database works in persistence mode, data is not loss between app start-stop. 

#### Call endpoints
- Swagger UI is available at http://localhost:8080/swagger-ui.html
- Postman (preferable since Swagger has its defects)

#### Db console
When App is running H2 database has UI available at http://localhost:8080/h2-console

#### Logs
Application logs are written to stdout


### API

#### Create new user
```
POST http://localhost:8080/users
```

Creates new user. Method is always unsecured. User id is returned.
Input data example:
```json
{
  "name": "Petr",
  "surname": "Petrov",
  "email": "petr.petrov@y.ru",
  "password": "qwerty"
}
```

#### Create new meeting
```
POST http://localhost:8080/meetings
```

Creates new meeting with specified parameters and participants. 
Returns single id for non-repeatable meetings and list of ids for repeatable.

When new meeting is created all participants have 'TENTATIVE' status and are considered to visit the meeting.

Supported recurrence values: 
- NONE (meeting occurs once)
- DAILY (meeting occurs every day starting from ${fromTime} during ${calendar.recurrence.duration.days} days)

Supported visibility values:
- PUBLIC (everyone can see meeting details)
- PRIVATE (only participants can see meeting details)
 
Input data example:
```json
{
  "title": "One-2-One",
  "organizer": "kris.petrova@d.ru",
  "fromTime": "2022-04-18 12:00",
  "toTime": "2022-04-18 12:30",
  "location": "Room",
  "visibility": "PRIVATE",
  "participants": [
    "petr.petrov@y.ru"
  ],
  "recurrence": "NONE"
}
```

#### Get meeting details
```
GET http://localhost:8080/meetings/{meetingId}/{meetingSubId}
```

Returns meeting details by provided id pair. For non-repeatable meetings ${meetingSubId} is always 1.

If meeting visibility is set to PRIVATE then only meeting participants are allowed to see meeting details.

#### Respond to the invitation
```
POST http://localhost:8080/meetings/response
```
Supported responses:
- TENTATIVE - default status for every new meeting. Users with 'TENTATIVE' status are considered to visit meeting.
- ACCEPTED - indicated that user accepted the meeting
- DECLINED - declined meetings are no longer the part of user calendar

For repeatable meetings it is possible to respond either to all meetings at once or to particular meeting from series.

Input data example to decline particular meeting:
```json
{
  "meetingId": 1,
  "meetingSubId": 3,
  "user": "petr.petrov@y.ru",
  "response": "DECLINED"
}
```

Input data example to decline the whole series:
```json
{
  "meetingId": 1,
  "meetingSubId": -1,
  "user": "petr.petrov@y.ru",
  "response": "DECLINED"
}
``` 

#### Get user calendar
```
GET http://localhost:8080/meetings/calendar/{userEmail}?fromTime=<>&toTime=<>
```
Build the list os user's meetings within the provided time interval. 

Both fromTime and toTime parameters are optional. If not specified, 
defaults to start of the day or end of the day respectively. 

Only 'TENTATIVE' and 'ACCEPTED' meetings are subject to be in user's calendar.

In case of PRIVATE meeting is in user's calendar, its details are not shown if calendar was requested by someone else 
and not by user himself. In this case only meeting time interval is shown. Example:
```json
{
        "meetingId": 3,
        "meetingSubId": 1,
        "title": "Spring Demo",
        "organizer": "kris.petrova@d.ru",
        "fromTime": "2022-04-18 15:00",
        "toTime": "2022-04-18 16:00"
    },
    {
        "meetingId": -1,
        "meetingSubId": -1,
        "fromTime": "2022-04-18 12:00",
        "toTime": "2022-04-18 12:30"
    }
]
```
For PUBLIC meeting all details are in place but for PRIVATE only time interval is shown. Meeting ids with -1 values 
are not valid and no meeting can be returned by these identifiers.

#### Meeting slot suggestion
```
GET http://localhost:8080/meetings/suggestion
```
***This method does not work correctly from Swagger UI, please use Postman instead***

For provided list of users and meeting duration searches for the nearest common free slot. 
Search granularity is controlled by ${calendar.minimal.meeting.slot.minutes} property.
If there is no free slot today, then will go the next day and so on.

 Input data example:
 ```json
{
  "durationMin": 30,
  "participants": [
    "anton.ivanov@g.ru",
    "victor.kiselev@y.ru",
    "petr.petrov@y.ru"
  ]
}
 ``` 