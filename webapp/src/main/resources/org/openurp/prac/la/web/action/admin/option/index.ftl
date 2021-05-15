[#ftl]
[@b.head/]
[@b.toolbar title="参选单位"/]
[@urp_base.semester_bar name="semester.id" value=currentSemester/]
[#assign sessionList=sessions?sort_by("beginAt")?reverse/]
   <div class="search-container">
     <div class="search-panel">
    [@b.form name="laOptionSearchForm" action="!search" target="laOptionlist" title="ui.searchForm" theme="search"]
      <input type="hidden" name="laOption.semester.id" value="${currentSemester.id}"/>
      [@b.select label="批次" name="laOption.session.id" items=sessionList/]
      [@b.textfields names="laOption.corporation.name;名称"/]
      [@b.select label="报名状态" name="signup_status" items={}]
        <option value="2">...</option>
        <option value="1">&gt;=面试人数</option>
        <option value="0">未满</option>
      [/@]
      [@b.select label="面试人数" name="enroll_status" items={}]
        <option value="2">...</option>
        <option value="1">已满</option>
        <option value="0">未满</option>
      [/@]
      [@b.textfields names="laOption.requirement;要求"/]
      <input type="hidden" name="orderBy" value="corporation.name desc"/>
    [/@]
     </div>
     <div class="search-list">
       [@b.div id="laOptionlist" href="!search?orderBy=corporation.name desc&laOption.semester.id="+currentSemester.id/]
     </div>
    </div>
[@b.foot/]
