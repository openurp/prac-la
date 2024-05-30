[#ftl]
[@b.head/]
[@b.toolbar title="法律助理报名批次"/]
[@base.semester_bar name="semester.id" value=currentSemester/]
   <div class="search-container">
     <div class="search-panel">
    [@b.form name="laSessionSearchForm" action="!search" target="laSessionlist" title="ui.searchForm" theme="search"]
      <input type="hidden" name="laSession.semester.id" value="${currentSemester.id}"/>
      [@b.textfields names="laSession.name;名称"/]
      <input type="hidden" name="orderBy" value="name desc"/>
    [/@]
     </div>
     <div class="search-list">
       [@b.div id="laSessionlist" href="!search?laSession.semester.id="+currentSemester.id +"&orderBy=name desc"/]
     </div>
    </div>
[@b.foot/]
