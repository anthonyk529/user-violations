class UserViolationsSuperController < ApplicationController

  protected
  
  #get projects
  def get_projects(filter_id)
  	projects=Array.new
  	filter = MeasureFilter.find_by_id(filter_id.to_i) 
	if filter
		filter.load_criteria_from_data   	
		filter.set_criteria_value(:qualifiers, 'TRK')
		filter.execute(self, :user => current_user)
		filter.rows.each do |row|
			projects << Project.by_key(row.snapshot.resource_id)
		end		
	end
	return projects
  end

  #get the first previous analysis before the given date
  def first_previous_analysis(date, snapshots)  	
  	previous_snapshots = snapshots.partition{|snap| (snap.created_at.to_date<=>jQuery_to_date(date)) <= 0}
  	if !previous_snapshots[0].empty?  		
  		previous_snapshots[0].reverse.each do |snap|
  			if !snap.measure('violations_by_author_sev').blank?
  				return snap  					
  			end
  		end
  	end  	
  	return nil
  end
  
  private
  
  def jQuery_to_date(string)
  	if string.empty?
  		return Date.today
  	else  	
  		#return Date.strptime(string, '%m/%d/%Y')
  		return Date.strptime(string, '%Y-%m-%d')
  	end
  end
  
end