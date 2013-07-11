class UserViolationsListController < UserViolationsSuperController
	
  def index
  
	@filtered_results=Array.new
	
	@created_before=params[:created_before]
  	@created_after=params[:created_after]
  	@dev=params[:dev]
  	@filter_id=params[:filter_id].to_i
  	@project_id=params[:project_id].to_i
  	@severity=params[:severity]
  	
  	parameters = {}
  	
  	projects=Array.new
  	if @project_id != 0
  		project = Project.by_key(@project_id.to_i)  		
  		projects << project
  	else
  		projects = get_projects(@filter_id)
  	end
  	
  	parameters['severities'] = @severity	
	pageSize=100
	parameters['pageSize'] = pageSize
	parameters['statuses']= ['OPEN', 'CONFIRMED', 'REOPENED']
   	
   	projects.each do |project|   	
   		parameters['componentRoots']=project.kee
	   	
	  	if @project_id != 0
	  		# for a single project dates are those of first previous snapshots
	  		parameters['createdBefore'] = @created_before
	  		parameters['createdAfter'] = @created_after
	  	else
	  		# for filters (several projects) dates are those given by the user
	  		# we must retrieve first previous snapshots for each projects
	  		snapshots = project.processed_snapshots
    		parameters['createdBefore'] = first_previous_analysis(@created_before, snapshots).created_at.tomorrow.strftime('%Y-%m-%d')
    		if !@created_after.eql?('')
    			snapshot = first_previous_analysis(@created_after, snapshots)
    			if snapshot 
    				parameters['createdAfter'] = snapshot.created_at.strftime('%Y-%m-%d')
    			end
	  		end
	  	end
	  	
	  	pageIndex=1
		parameters['pageIndex'] = pageIndex
	  	
	  	@res=Api.issues.find(parameters)
		@results = Api.issues.find(parameters).issues().to_a	
		
		while (pageIndex*pageSize)<@res.paging.total.to_i do
			pageIndex += 1
			parameters['pageIndex'] = pageIndex
			temp_results = Api.issues.find(parameters).issues().to_a
			@results += Api.issues.find(parameters).issues().to_a		
		end
	  	
	  	if @results.length > 0  		
	  		previous_component = @results[0].componentKey
	  		current_component = previous_component	  	
		  	authors = Project.by_key(previous_component).last_snapshot.measure('authors_by_line').data
		  	@hash = authors_to_hash(authors)
		  	
		  	@results.each do |result|	  		
		  		previous_component = current_component
		  		current_component = result.componentKey
		  		
		  		if !current_component.eql?previous_component  			
		  			authors = Project.by_key(current_component).last_snapshot.measure('authors_by_line').data
		  			@hash = authors_to_hash(authors)
		  		end
		  		if result.line
		  			if @hash[result.line] == @dev	  				
		  				@filtered_results << result	  				
		  			end
		  		end
		  	end
		end
	end 	
  end
  
  def do_assign
  	@assign_results = Array.new
  	
  	created_after=params[:created_after]
  	created_before=params[:created_before]
  	dev = params[:dev]
  	filter_id=params[:filter_id]
  	project_id=params[:project_id]
  	severity=params[:severity]
  	
  	if params[:issues].present?
  		@assignee = params[:assignee]
  		@issues_to_assign = params[:issues]  	
  	
	  	@issues_to_assign.each do |issue_key|  		
	  		issue_result = Internal.issues.assign(issue_key, @assignee)
	  		@assign_results << issue_result
	  	end
  	else flash[:error]=message('user-violations.noissueselected')
  	end  	  	
  	redirect_to :action => 'index', :dev => dev, :filter_id => filter_id, :project_id => project_id, :severity => severity, :created_after => created_after, :created_before => created_before
  end 

  private
  
  def authors_to_hash(authors_by_line)
  	hash = authors_by_line.split(";").each_with_object({}) do |str, h| 
  		k,v = str.split("=")
  		h[k.to_i] = v
	end
	return hash
  end
end