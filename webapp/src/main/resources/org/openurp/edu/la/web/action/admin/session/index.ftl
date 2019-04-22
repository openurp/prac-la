[#ftl]
[@b.head/]
[@b.toolbar title="选择批次"/]
<table class="indexpanel">
  <tr>
    <td class="index_view" >
    [@b.form name="laSessionSearchForm" action="!search" target="laSessionlist" title="ui.searchForm" theme="search"]
      [@b.textfields names="laSession.name;名称"/]
      [@b.select label="学年学期" name="laSession.semester.id" items={}]
        <option value="">...</option>
        [#list semesters?sort_by("code")?reverse as semester]
          <option value="${semester.id}" [#if semester.id = currentSemester.id]selected[/#if]>${(semester.schoolYear)!}学年${(semester.name?replace('0','第'))!}学期</option>
        [/#list]
      [/@]
      <input type="hidden" name="orderBy" value="name desc"/>
    [/@]
    </td>
    <td class="index_content">[@b.div id="laSessionlist" href="!search?orderBy=name desc"/]
    </td>
  </tr>
</table>
[@b.foot/]
