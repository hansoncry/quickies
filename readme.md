1. Get the log files from QA3 in to the project dir.
```bash
cd ~/Projects/quickies
scp q3:/usr/local/jetty/logs/2015_03_12* .
2015_03_12.stderrout.log                                                                                                                                                                                                                                                                                                                       100%   32MB  10.6MB/s   00:03    
2015_03_12.stderrout.log.092332877
```
         
2. Then run the RpcEvents#main and you'll get all the RPC events into a H2 database. I checked in IDEA's project, so you'll have a run configuration and the data source already setup.
You can run queries like
```sql
SELECT *
FROM EVENT
ORDER BY action, callback, EVENT_TIME ASC;


SELECT * FROM (
SELECT
  action,
  callback,
  GROUP_CONCAT(EVENT_NAME ORDER BY EVENT_TIME SEPARATOR ' -> ') AS event_sequence ,
  GROUP_CONCAT(EVENT_TIME ORDER BY EVENT_TIME SEPARATOR ' -> ') AS event_times
FROM (SELECT *
      FROM EVENT
      ORDER BY EVENT_TIME ASC
)
GROUP BY action, callback
)
WHERE CALLBACK like '%ChartMainPresenter%'
--    OR CALLBACK like '%ChartEncounterPresenter%'
--    OR CALLBACK like '%ChartEncounterPresenter%'
--    OR CALLBACK like '%ChartTimelinePresenter' 
--    OR CALLBACK like '%ChartSummaryForFlowsheetWidgetPresenter' 
--    OR CALLBACK like '%ChartSummaryWidgetPresenter' 
--    OR CALLBACK like '%AddAllergyPresenter' 
--    OR CALLBACK like '%AllergyListPresenter' 
--    OR CALLBACK like '%AllergyPresenter'
AND ( ACTION like 'com.nightingale.remote.rpc.action.PatientEncounter_search%' OR ACTION like 'com.nightingale.remote.rpc.action.PatientEncounter_getIDs%') 
;
```
