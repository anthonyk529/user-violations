class UserViolationsController < UserViolationsSuperController
  def index
  	@myFilter = params[:filter]
    @myProject_id = params[:pr]
    dateDeb = params[:dd]
    dateFin = params[:df]
    @given_date = Array.new
	@given_date << dateFin
	@given_date << dateDeb
	
    #projet = Project.by_key(@myProject_id.to_i)
	@previous_snapshots=Array.new
	
	@dates = Hash.new {|h,k| h[k]=[]}
    
    if @myProject_id.to_i==0
    	all_projects = get_projects(@myFilter)
    	myHashFin = compute_sum(all_projects, dateFin)    	
    	if !dateDeb.empty?
    		myHashDeb = compute_sum(all_projects, dateDeb)
    		@myHash = compute_difference(myHashFin, myHashDeb)
    	else
    		@myHash = myHashFin
    	end
    else
    	project = Project.by_key(@myProject_id.to_i)
    	data = project.processed_snapshots
    	snapshot = first_previous_analysis(dateFin, data)
    	@previous_snapshots << snapshot
    	list = get_violations_data(snapshot)
    	myHashFin = Hash.new
    	if !list.empty?
    		myHashFin = JSON.parse(list)
    	end
    	if !dateDeb.empty?
    		snapshot = first_previous_analysis(dateDeb, data)
    		@previous_snapshots << snapshot    		
    		list = get_violations_data(snapshot)
    		myHashDeb = Hash.new
    		if !list.empty?
    			myHashDeb = JSON.parse(list)    			
    		end
    		@myHash = compute_difference(myHashFin, myHashDeb)
    	else
    		@myHash = myHashFin
    	end    		
    end
    render :partial => 'result', :myHash => @myHash, :previous_snapshots => @previous_snapshots, :given_date => @given_date,  :myFilter => @myFilter, :myProject_id => @myProject_id
  end
  
  
  private
  
  # compute the total number of violations for every project.
  # data for every project = first previous snapshot data before the given date
  def compute_sum(projects, date)
  	myHash=Hash.new
  	projects.each do |project|
    	snapshots = project.processed_snapshots
    	snapshot = first_previous_analysis(date, snapshots)
    	list = get_violations_data(snapshot)
    	#hash_data = get_violations_data(snapshot)
    	if !list.empty?
	    	hash_data = JSON.parse(list)
	    	hash_data.each do |name, violationsList|	    	
		    	if myHash.has_key?(name)
					myHash[name]=myHash[name].zip(violationsList).map { |x, y| x + y }
				else				
					myHash.merge!(Hash[name => violationsList])
				end
			end
		end
    end
    return myHash
  end
  
  # compute the difference between two hashes
  # hash1 = { "aa" => [4,0,2,0,1,1], "bb" => [3,0,2,0,1,0]}
  # hash2 = { "aa" => [3,0,1,1,1,0], "cc" => [1,1,0,0,0,0]}
  # result = { "aa" => [1,0,1,-1,0,1], "bb" => [3,0,2,0,1,0], "cc" => [-1,-1,0,0,0,0]}    
  def compute_difference(hash1, hash2)  	
  	hash2.each do |name, violationsList|
  		if hash1.has_key?(name)
  			hash1[name]=hash1[name].zip(violationsList).map! { |x, y| x - y }
  		else
  			hash1.merge!(Hash[name => violationsList.map! {|x| -x}])
  		end	
  	end
  	return hash1
  end  
  
  
  #return a hash corresponding to violations_by_author_sev data for a given snapshot
  def get_violations_data(snapshot)
  	if snapshot	  	
	  	if !snapshot.measure('violations_by_author_sev').data.blank?
	  		data = snapshot.measure('violations_by_author_sev').data
	  	else
	  		data = snapshot.measure('violations_by_author_sev').text_value
	  	end	  		
	else 
		data= ''	  	
	end	
	return data
  end
  
  
end