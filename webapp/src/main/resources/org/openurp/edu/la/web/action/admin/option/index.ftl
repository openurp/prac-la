[#ftl]
[@b.head/]
[@b.toolbar title="参选单位"/]
<table class="indexpanel">
  <tr>
    <td class="index_view" >
    [@b.form name="laOptionSearchForm" action="!search" target="laOptionlist" title="ui.searchForm" theme="search"]
      [@b.textfields names="laOption.corporation.name;名称"/]
      [@b.select label="学年学期" name="lecture.semester.id" items={}]
        <option value="">...</option>
        [#list semesters?sort_by("code")?reverse as semester]
          <option value="${semester.id}" [#if semester.id = currentSemester.id]selected[/#if]>${(semester.schoolYear)!}学年${(semester.name?replace('0','第'))!}学期</option>
        [/#list]
      [/@]
      [@b.select label="报名状态" name="status" items={}]
        <option value="2">...</option>
        <option value="1">已满</option>
        <option value="0">未满</option>
      [/@]
      <input type="hidden" name="orderBy" value="corporation.name desc"/>
    [/@]
    </td>
    <td class="index_content">[@b.div id="laOptionlist" href="!search?orderBy=corporation.name desc"/]
    </td>
  </tr>
</table>
[@b.foot/]
