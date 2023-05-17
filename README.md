# NewsApp parent

NewsApp parent used to manipulate and gather data for solr indexing

## Modules

###Newsbox-aggregator
   - Used to fetch data from news API and store at a particular location.
   - NewsAPIFetcher is the main class and can be run in two ways
     - Without parameters : where it will gather todays data and store
     - With parameters : [start-date] [end-date] [qParameter]
     
###Newsbox-indexing
   - Used for indexing gathered data to apache solr
   - DailySolrIndexer & FullSolrIndexer can be run respectively for daily use and full indexing.
   - DailySolrIndexer: Runs for indexing data of that particular date ie today
   - FullSolrIndexer : Runs for indexing all data to solr 

###solrconfig
   - Contains solr config data, when setting up new solr ,
     cd solr-8.11.2/server/solr
   - mkdir newsfeed
   - cp {project-root}/solrconfig/src/main/resources /solr-8.11.2/server/solr/newsfeed/




## Design
![Architecture Diagram](https://github.com/subhajit1997/newsbox-django/blob/master/newsbox/architecture.png)


## Setup and Installation



