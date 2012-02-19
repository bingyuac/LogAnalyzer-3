package net.moc.LogAnalyzer;

import java.io.IOException;


import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import nl.bitwalker.useragentutils.*;
import com.maxmind.geoip.*;

/*
 * Class for mapping logentries to CountryCodes. 
 */
public class GeoIpCountryMapper extends Mapper<LongWritable, Text, Text, LongWritable> {
	
	LookupService cl;
	
	public GeoIpCountryMapper() {
		try {
			String dbfile =  LogAnalyzer.class.getResource("/res/GeoIP.dat").toString().substring(5);
			cl = new LookupService(dbfile,LookupService.GEOIP_MEMORY_CACHE);
		} catch (Exception e) {
			System.err.println("Error opening GeoIP data file.");
			System.err.println(e);
			System.exit(2);
		}
	}
	
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		try {
			ApacheCombinedLogEntryInterface logEntry = ApacheCombinedLogEntryFactory.getLogEntryFromString(value.toString());
			context.write(new Text(cl.getCountry(logEntry.getClientIp()).getCode()) , new LongWritable(1));			
		} catch ( Exception e) {
			System.err.println("Error parsing logstash outpu");
			context.write(new Text("Unknown (parse error in logentry") , new LongWritable(1));
		}
	}
}